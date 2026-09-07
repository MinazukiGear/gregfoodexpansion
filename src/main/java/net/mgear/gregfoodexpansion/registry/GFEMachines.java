package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.baking.GFEOvenPatterns;
import net.mgear.gregfoodexpansion.baking.TunnelOvenMachine;
import net.minecraft.world.level.block.SoundType;

/**
 * 切配机(food-processor.md §2):全 tier ULV–UV 共 9 档单方块,
 * LV 前食品加工仅可手搓的边界见 compatibility-boundary.md Q1——ULV 档仅供
 * EMI 展示与后续内容预留,生存获取配方随 M1 烘焙工业化补齐(当前无合成配方)。
 * 无流体 IO,hull 模型 + 自有 overlay 贴图(tier 底壳由 GTCEu 自动分档)。
 * 通用烹饪机(universal-cooker.md §2)同构:c1 煮 / c2 蒸 / c3 炒 / c4 炸。
 * 隧道式烤炉(tunnel-oven.md §2):多方块无 tier,复用 cooking 类型仅跑电路 5。
 */
public final class GFEMachines {
    private GFEMachines() {}

    public static MachineDefinition[] FOOD_PROCESSOR;
    public static MachineDefinition[] UNIVERSAL_COOKER;
    public static MultiblockMachineDefinition TUNNEL_OVEN;

    /** 由 GTCEuAPI RegisterEvent<MachineDefinition> 触发(入口类监听)。 */
    public static void init() {
        int[] tiers = {GTValues.ULV, GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV,
                GTValues.IV, GTValues.LuV, GTValues.ZPM, GTValues.UV};

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
                tiers);

        UNIVERSAL_COOKER = GTMachineUtils.registerTieredMachines(
                GFERegistration.REGISTRATE,
                "universal_cooker",
                (holder, tier) -> new SimpleTieredMachine(holder, tier,
                        GTMachineUtils.defaultTankSizeFunction),
                (tier, builder) -> {
                    builder.recipeModifier(GTRecipeModifiers.OC_NON_PERFECT);
                    return builder
                            .langValue("%s %s %s".formatted(GTValues.VLVH[tier],
                                    "Universal Cooker", GTValues.VLVT[tier]))
                            .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(
                                    GregFoodExpansion.id("universal_cooker"), GFERecipeTypes.COOKING))
                            .rotationState(RotationState.NON_Y_AXIS)
                            .recipeType(GFERecipeTypes.COOKING)
                            .workableTieredHullModel(
                                    GregFoodExpansion.id("block/machines/universal_cooker"))
                            .register();
                },
                tiers);

        TUNNEL_OVEN = GFERegistration.REGISTRATE
                .multiblock("tunnel_oven", TunnelOvenMachine::new)
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(GFERecipeTypes.COOKING)
                .recipeModifier(TunnelOvenMachine::modifyRecipe)
                .appearanceBlock(GFEBlocks.TUNNEL_OVEN_CASING)
                .blockProp(properties -> properties.strength(5.0F, 6.0F).sound(SoundType.METAL))
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(GTMachineModels.createWorkableCasingMachineModel(
                        GregFoodExpansion.id("block/casings/tunnel_oven_casing"),
                        GregFoodExpansion.id("block/machines/tunnel_oven")))
                .pattern(GFEOvenPatterns::createPattern)
                .langValue("Tunnel Oven")
                .allowCoverOnFront(false)
                .register();
    }
}
