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

/** LV workshops with structural processing segments; the MV tunnel oven remains a skeleton. */
public final class GFMachines {
    public static MultiblockMachineDefinition PREP_WORKSHOP;
    public static MultiblockMachineDefinition COOKING_WORKSHOP;
    public static MultiblockMachineDefinition TUNNEL_OVEN;

    private GFMachines() {}

    public static void init() {
        // LV 厨房两台用钢机壳:不锈钢为 MV 档材料,LV 时代不可制作
        PREP_WORKSHOP = GFERegistration.REGISTRATE
                .multiblock("prep_workshop", holder -> new net.mgear.gregfoodexpansion.content.runtime.WorkshopMachine(holder, "rolling"))
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(GFRecipeTypes.PREP_WORKSHOP_RECIPES)
                .recipeModifier(net.mgear.gregfoodexpansion.content.runtime.WorkshopMachine::modify, true)
                .tier(GTValues.LV)
                .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
                .blockProp(properties -> properties.strength(5.0F, 6.0F).sound(net.minecraft.world.level.block.SoundType.METAL))
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(GTMachineModels.createWorkableCasingMachineModel(
                        GregFoodExpansion.gtceuId("block/casings/solid/machine_casing_solid_steel"),
                        GregFoodExpansion.gtceuId("block/multiblock/steam_grinder")))
                .pattern(definition -> WorkshopPatterns.pattern(definition, false))
                .shapeInfos(definition -> java.util.List.of(WorkshopPatterns.shape(definition, false, 1), WorkshopPatterns.shape(definition, false, 4)))
                .langValue("Prep Workshop")
                .tooltips(Component.translatable("gregfoodexpansion.workshop.rolling_help"))
                .allowCoverOnFront(false)
                .register();

        COOKING_WORKSHOP = GFERegistration.REGISTRATE
                .multiblock("cooking_workshop", holder -> new net.mgear.gregfoodexpansion.content.runtime.WorkshopMachine(holder, "boiling"))
                .rotationState(RotationState.NON_Y_AXIS)
                .recipeType(GFRecipeTypes.COOKING_WORKSHOP_RECIPES)
                .recipeModifier(net.mgear.gregfoodexpansion.content.runtime.WorkshopMachine::modify, true)
                .tier(GTValues.LV)
                .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
                .blockProp(properties -> properties.strength(5.0F, 6.0F).sound(net.minecraft.world.level.block.SoundType.METAL))
                .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
                .model(GTMachineModels.createWorkableCasingMachineModel(
                        GregFoodExpansion.gtceuId("block/casings/solid/machine_casing_solid_steel"),
                        GregFoodExpansion.gtceuId("block/multiblock/multi_furnace")))
                .pattern(definition -> WorkshopPatterns.pattern(definition, true))
                .shapeInfos(definition -> java.util.List.of(WorkshopPatterns.shape(definition, true, 1), WorkshopPatterns.shape(definition, true, 4)))
                .langValue("Cooking Workshop")
                .tooltips(Component.translatable("gregfoodexpansion.workshop.boiling_help"))
                .allowCoverOnFront(false)
                .register();

        // MV 隧道烤炉用洁净不锈钢:不锈钢 MV 可制作,与机器档位同轴
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
