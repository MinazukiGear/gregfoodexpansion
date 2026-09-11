package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.mgear.gregfoodexpansion.GregFoodExpansion;

/**
 * M1' 三台多方块骨架(machines.md §1 #1/#2/#5,结构细则 §5.1–§5.3):
 *
 * <ul>
 *   <li>切配工坊(LV)/烹饪工坊(LV):3×3×3 工坊级骨架,结构化模块段(刀工/绞碎/…、
 *       煮/蒸/炒/炸)随配方批次以结构段形式追加;</li>
 *   <li>隧道烤炉(MV):5×3×3 隧道机架,炉温段模块沿长向扩展(段数=并行,强制预热待配方逻辑)。</li>
 * </ul>
 *
 * <p>骨架阶段:配方类型已注册、机器可放置成行并空转;控制器贴图暂复用 GTCEu 既有
 * overlay(机器贴图按 content-pipeline.md §4 属全手绘管线,随美术批次替换);
 * 外壳暂用洁净不锈钢(食品级意象),专属外壳随美术批次定案。</p>
 */
public final class GFMachines {
    public static MultiblockMachineDefinition PREP_WORKSHOP;
    public static MultiblockMachineDefinition COOKING_WORKSHOP;
    public static MultiblockMachineDefinition TUNNEL_OVEN;

    private GFMachines() {}

    public static void init() {
        PREP_WORKSHOP = GFERegistration.REGISTRATE
                .multiblock("prep_workshop", WorkableElectricMultiblockMachine::new)
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(GFRecipeTypes.PREP_WORKSHOP_RECIPES)
                .tier(GTValues.LV)
                .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
                .blockProp(properties -> properties.strength(5.0F, 6.0F).sound(net.minecraft.world.level.block.SoundType.METAL))
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(GTMachineModels.createWorkableCasingMachineModel(
                        GregFoodExpansion.gtceuId("block/casings/solid/machine_casing_clean_stainless_steel"),
                        GregFoodExpansion.gtceuId("block/multiblock/steam_grinder")))
                .pattern(definition -> GFPatterns.prepWorkshop(definition,
                        GTBlocks.CASING_STAINLESS_CLEAN.get()))
                .shapeInfos(definition -> java.util.List.of(
                        GFPatterns.workshopShape(definition, GTBlocks.CASING_STAINLESS_CLEAN.get())))
                .langValue("Prep Workshop")
                .tooltips(skeletonNote())
                .allowCoverOnFront(false)
                .register();

        COOKING_WORKSHOP = GFERegistration.REGISTRATE
                .multiblock("cooking_workshop", WorkableElectricMultiblockMachine::new)
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(GFRecipeTypes.COOKING_WORKSHOP_RECIPES)
                .tier(GTValues.LV)
                .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
                .blockProp(properties -> properties.strength(5.0F, 6.0F).sound(net.minecraft.world.level.block.SoundType.METAL))
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(GTMachineModels.createWorkableCasingMachineModel(
                        GregFoodExpansion.gtceuId("block/casings/solid/machine_casing_clean_stainless_steel"),
                        GregFoodExpansion.gtceuId("block/multiblock/multi_furnace")))
                .pattern(definition -> GFPatterns.cookingWorkshop(definition,
                        GTBlocks.CASING_STAINLESS_CLEAN.get()))
                .shapeInfos(definition -> java.util.List.of(
                        GFPatterns.workshopShape(definition, GTBlocks.CASING_STAINLESS_CLEAN.get())))
                .langValue("Cooking Workshop")
                .tooltips(skeletonNote())
                .allowCoverOnFront(false)
                .register();

        TUNNEL_OVEN = GFERegistration.REGISTRATE
                .multiblock("tunnel_oven", WorkableElectricMultiblockMachine::new)
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(GFRecipeTypes.TUNNEL_OVEN_RECIPES)
                .tier(GTValues.MV)
                .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
                .blockProp(properties -> properties.strength(5.0F, 6.0F).sound(net.minecraft.world.level.block.SoundType.METAL))
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(GTMachineModels.createWorkableCasingMachineModel(
                        GregFoodExpansion.gtceuId("block/casings/solid/machine_casing_clean_stainless_steel"),
                        GregFoodExpansion.gtceuId("block/multiblock/pyrolyse_oven")))
                .pattern(definition -> GFPatterns.tunnelOven(definition,
                        GTBlocks.CASING_STAINLESS_CLEAN.get()))
                .shapeInfos(definition -> java.util.List.of(
                        GFPatterns.tunnelOvenShape(definition, GTBlocks.CASING_STAINLESS_CLEAN.get())))
                .langValue("Tunnel Oven")
                .tooltips(skeletonNote())
                .allowCoverOnFront(false)
                .register();
    }

    private static Component[] skeletonNote() {
        return new Component[] {
                Component.translatable("gregfoodexpansion.machine.skeleton_note").withStyle(ChatFormatting.GRAY)};
    }
}
