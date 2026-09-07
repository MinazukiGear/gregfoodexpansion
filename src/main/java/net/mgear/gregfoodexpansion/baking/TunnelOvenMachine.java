package net.mgear.gregfoodexpansion.baking;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import net.mgear.gregfoodexpansion.GregFoodExpansion;

import java.util.Iterator;

/**
 * 隧道式烤炉控制器(tunnel-oven.md):可变长度隧道多方块,三档炉温 + 段数并行。
 *
 * <p>炉温状态机 [三档已定案,数值草案]:冷炉 → 预热(集中耗能 64 EU/t,
 * 每档 600 tick,合计 1,800 tick,显著长于单批烘烤)→ 保温(8 EU/t 低耗维持);
 * 断电累计 600 tick 熄火降温,重开需重新预热。v1 一次预热至高温档
 * (名义温度为风味口径,分档精确控制在后续版本)。</p>
 *
 * <p>并行 [已定案]:段数 × 2(3 段起步 6 并行,9 段满载 18 并行),
 * 由 {@link ParallelLogic#getParallelAmount} 按实际可用原料收敛。</p>
 */
public class TunnelOvenMachine extends WorkableMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            TunnelOvenMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int TARGET_TEMP = 3;
    public static final int PREHEAT_EU_PER_TICK = 64;
    public static final int MAINTAIN_EU_PER_TICK = 8;
    public static final int PREHEAT_TICKS_PER_TIER = 600;
    public static final int COOLDOWN_LACK_TICKS = 600;

    @Persisted
    private int currentTemp = 0;
    @Persisted
    private int preheatProgress = 0;
    @Persisted
    private int lackingTicks = 0;

    private int segmentCount = 3;
    private TickableSubscription thermalSubscription;

    public TunnelOvenMachine(com.gregtechceu.gtceu.api.machine.IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new TunnelOvenRecipeLogic(this);
    }

    @Override
    public TunnelOvenRecipeLogic getRecipeLogic() {
        return (TunnelOvenRecipeLogic) super.getRecipeLogic();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        // 段数 = 排气口数 ÷ 2(每段烟囱 2 格,见 GFEOvenPatterns)
        segmentCount = Math.max(3, getMultiblockState().getMatchContext().getInt("VentCount") / 2);
        thermalSubscription = subscribeServerTick(thermalSubscription, this::tickThermal);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        // 断段即熄火:结构失效温度归零,重成型需重新预热
        currentTemp = 0;
        preheatProgress = 0;
        lackingTicks = 0;
        if (thermalSubscription != null) {
            thermalSubscription.unsubscribe();
        }
    }

    private void tickThermal() {
        if (!isFormed()) return;
        // 工作中的热能由配方 EU/t 承载,只在不工作的 idle 段结算预热/保温
        if (getRecipeLogic().isWorking()) {
            lackingTicks = 0;
            return;
        }
        if (currentTemp < TARGET_TEMP) {
            long removed = drainEnergy(PREHEAT_EU_PER_TICK);
            if (removed >= PREHEAT_EU_PER_TICK) {
                preheatProgress++;
                if (preheatProgress >= TARGET_TEMP * PREHEAT_TICKS_PER_TIER) {
                    currentTemp = TARGET_TEMP;
                    preheatProgress = 0;
                }
            }
            // 预热期能量不足则原地暂停,进度不清零
        } else {
            long removed = drainEnergy(MAINTAIN_EU_PER_TICK);
            if (removed >= MAINTAIN_EU_PER_TICK) {
                lackingTicks = 0;
            } else if (++lackingTicks > COOLDOWN_LACK_TICKS) {
                // 断电缓慢降温:累计 600 tick 无力保温即熄火
                currentTemp = 0;
                preheatProgress = 0;
                lackingTicks = 0;
            }
        }
    }

    /** 从全部能量仓逐个抽取 EU,返回实际抽出量。 */
    private long drainEnergy(long amount) {
        long remaining = amount;
        for (var handlers : getCapabilitiesProxy().get(IO.IN)) {
            if (handlers instanceof IEnergyContainer container) {
                remaining -= container.removeEnergy(remaining);
                if (remaining <= 0) break;
            }
        }
        return amount - remaining;
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    /** 并行 = 段数 × 2(tunnel-oven.md §5 已定案)。 */
    public int getMaxParallel() {
        return segmentCount * 2;
    }

    public int getCurrentTempTier() {
        return currentTemp;
    }

    /** 并行修饰器:N 倍产出 / N 倍输入 / N 倍 EU,时长不变(GTCEu 并行语义)。 */
    public static ModifierFunction modifyRecipe(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof TunnelOvenMachine oven) || !oven.isFormed()) {
            return ModifierFunction.IDENTITY;
        }
        int parallel = ParallelLogic.getParallelAmount(oven, recipe, oven.getMaxParallel());
        if (parallel <= 1) return ModifierFunction.IDENTITY;
        return ModifierFunction.builder().parallels(parallel).build();
    }
}
