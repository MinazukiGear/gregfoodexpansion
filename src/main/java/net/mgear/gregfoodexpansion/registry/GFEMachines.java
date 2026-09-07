package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import net.mgear.gregfoodexpansion.GregFoodExpansion;

/**
 * 切配机(food-processor.md §2):全 tier ULV–UV 共 9 档单方块,
 * LV 前食品加工仅可手搓的边界见 compatibility-boundary.md Q1——ULV 档仅供
 * EMI 展示与后续内容预留,生存获取配方随 M1 烘焙工业化补齐(当前无合成配方)。
 * 无流体 IO,hull 模型 + 自有 overlay 贴图(tier 底壳由 GTCEu 自动分档)。
 */
public final class GFEMachines {
    private GFEMachines() {}

    public static MachineDefinition[] FOOD_PROCESSOR;

    /** 由 GTCEuAPI RegisterEvent<MachineDefinition> 触发(入口类监听)。 */
    public static void init() {
        FOOD_PROCESSOR = GTMachineUtils.registerTieredMachines(
                GFERegistration.REGISTRATE,
                "food_processor",
                (holder, tier) -> new SimpleTieredMachine(holder, tier,
                        GTMachineUtils.defaultTankSizeFunction),
                (tier, builder) -> {
                    builder.recipeModifier(GTRecipeModifiers.OC_NON_PERFECT);
                    return builder
                            .langValue("%s %s %s".formatted(GTValues.VLVH[tier],
                                    "Food Processor", GTValues.VLVT[tier]))
                            .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(
                                    GregFoodExpansion.id("food_processor"), GFERecipeTypes.FOOD_PREP))
                            .rotationState(RotationState.NON_Y_AXIS)
                            .recipeType(GFERecipeTypes.FOOD_PREP)
                            .workableTieredHullModel(
                                    GregFoodExpansion.id("block/machines/food_processor"))
                            .register();
                },
                new int[] {GTValues.ULV, GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV,
                        GTValues.IV, GTValues.LuV, GTValues.ZPM, GTValues.UV});
    }
}
