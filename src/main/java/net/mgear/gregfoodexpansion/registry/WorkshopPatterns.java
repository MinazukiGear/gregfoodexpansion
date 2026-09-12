package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.*;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.mgear.gregfoodexpansion.content.runtime.WorkshopMachine;
import java.util.HashSet;

/**
 * LV 工坊结构:正面层、仓室层、0–4 个加工层、封底层(拟真修订 2026-09-13)。
 *
 * <p>加工层在原有"8 钢机壳 + 中央模块"基础上增加正面底部固定工艺件,使每段读作真实设备:
 * 煮段底部为钢火箱灶眼(B),压延段底部为钢管辊道(R)。封底层 = 同布局去掉中央模块——
 * 该不变量保证"拆掉模块后该层退化为封底"的扩建/回退语义(WorkshopTests.rebuiltSegments)。</p>
 */
public final class WorkshopPatterns {
    public static final int MAX_SEGMENTS = 4;
    private WorkshopPatterns() {}

    public static Block moduleBlock(boolean cooking) {
        return cooking ? GTBlocks.CASING_BRONZE_PIPE.get() : GTBlocks.CASING_STEEL_GEARBOX.get();
    }

    /** 加工层正面底部固定工艺件:煮段 = 钢火箱灶眼,压延段 = 钢管辊道。 */
    public static Block fixtureBlock(boolean cooking) {
        return cooking ? GTBlocks.FIREBOX_STEEL.get() : GTBlocks.CASING_STEEL_PIPE.get();
    }

    private static char fixtureChar(boolean cooking) {
        return cooking ? 'B' : 'R';
    }

    public static BlockPattern pattern(MultiblockMachineDefinition definition, boolean cooking) {
        Block steel = GTBlocks.CASING_STEEL_SOLID.get();
        Block module = moduleBlock(cooking);
        char fixture = fixtureChar(cooking);
        var hatchable = Predicates.blocks(steel)
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(cooking ? 2 : 1))
                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(cooking ? 1 : 0));
        var segment = Predicates.custom(state -> {
            if (!state.getBlockState().is(module)) return false;
            state.getMatchContext().getOrCreate(WorkshopMachine.SEGMENTS_KEY, HashSet::new)
                    .add(state.getPos().immutable());
            return true;
        }, () -> new BlockInfo[]{BlockInfo.fromBlockState(module.defaultBlockState())})
                .addTooltips(Component.translatable("gregfoodexpansion.module." + (cooking ? "boiling" : "rolling")));
        String midRow = "S" + fixture + "S";
        // 字符串序 = 世界 y 自下而上:固定件(灶眼/辊道)落在加工层底层贴地位。
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .aisle("CCC", "CDC", "CCC")
                .aisle("CCC", "C#C", "CCC")
                .aisle(midRow, "SMS", "SSS").setRepeatable(0, MAX_SEGMENTS)
                .aisle(midRow, "SSS", "SSS")
                .where('C', hatchable)
                .where('S', Predicates.blocks(steel))
                .where('M', segment)
                .where(fixture, Predicates.blocks(fixtureBlock(cooking)))
                .where('#', Predicates.air())
                .where('D', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .build();
    }

    /** The same front-to-back convention is used in the pattern, preview and server tests. */
    public static MultiblockShapeInfo shape(MultiblockMachineDefinition definition, boolean cooking, int count) {
        if (count < 0 || count > MAX_SEGMENTS) throw new IllegalArgumentException("Invalid segment count");
        char fixture = fixtureChar(cooking);
        String midRow = "S" + fixture + "S";
        var builder = MultiblockShapeInfo.builder()
                .aisle("CCC", "IDO", "CCC")
                .aisle("CEC", "I#O", cooking ? "CWC" : "CCC");
        for (int i = 0; i < count; i++) builder.aisle(midRow, "SMS", "SSS");
        return builder.aisle(midRow, "SSS", "SSS")
                .where('C', GTBlocks.CASING_STEEL_SOLID.get())
                .where('S', GTBlocks.CASING_STEEL_SOLID.get())
                .where('M', moduleBlock(cooking))
                .where(fixture, fixtureBlock(cooking))
                .where('#', net.minecraft.world.level.block.Blocks.AIR)
                .where('I', GTMachines.ITEM_IMPORT_BUS[GTValues.LV], Direction.WEST)
                .where('O', GTMachines.ITEM_EXPORT_BUS[GTValues.LV], Direction.EAST)
                .where('E', GTMachines.ENERGY_INPUT_HATCH[GTValues.LV], Direction.DOWN)
                .where('W', GTMachines.FLUID_IMPORT_HATCH[GTValues.LV], Direction.UP)
                .where('D', definition, Direction.NORTH).build();
    }
}
