package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.content.GameplayAudit;
import net.mgear.gregfoodexpansion.content.runtime.GFContent;
import net.mgear.gregfoodexpansion.content.runtime.WorkshopMachine;
import java.util.function.Consumer;

/** Loaded through GT's runtime recipe pipeline; matrix ingredients remain the single source of truth. */
public final class GFMachineRecipes {
    private GFMachineRecipes() {}

    public static void register(Consumer<FinishedRecipe> out) {
        controller(out, false);
        controller(out, true);
        // GT already macerates wheat to wheat dust. A separate packer conversion avoids competing
        // with GT's wheat-grain mixer recipe for its own dough.
        GTRecipeTypes.PACKER_RECIPES.recipeBuilder(GregFoodExpansion.id("flour_from_wheat_dust"))
                .inputItems(TagPrefix.dust, GTMaterials.Wheat)
                .outputItems(GFContent.item("flour")).duration(20).EUt(8).save(out);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(GregFoodExpansion.id("dough"))
                .inputItems(GFContent.item("flour"), 4).inputFluids(GTMaterials.Water.getFluid(1000))
                .outputItems(GFContent.item("dough"), 4).duration(100).EUt(8).save(out);
        GFRecipeTypes.PREP_WORKSHOP_RECIPES.recipeBuilder(GregFoodExpansion.id("rolled_noodles"))
                .inputItems(GFContent.item("dough")).outputItems(GFContent.item("noodles"), 2)
                .addData(WorkshopMachine.MODULE_KEY, "rolling").duration(80).EUt(8).save(out);

        for (var dish : GFContent.TABLES.gameplay.machineDishes()) {
            var source = GFContent.TABLES.gameplay.potDishes().stream()
                    .filter(d -> d.id().equals(dish.source())).findFirst().orElseThrow();
            var row = GFContent.TABLES.matrixTables.stream().flatMap(t -> t.rows().stream())
                    .filter(r -> r.id().equals(dish.source())).findFirst().orElseThrow();
            var recipe = GFRecipeTypes.COOKING_WORKSHOP_RECIPES.recipeBuilder(GregFoodExpansion.id(dish.id()))
                    .addData(WorkshopMachine.MODULE_KEY, "boiling")
                    .inputItems(Items.BOWL, source.servings())
                    .inputFluids(GTMaterials.Water.getFluid(dish.water()))
                    .duration(dish.duration()).EUt(8);
            for (String ref : GameplayAudit.refs(row)) {
                recipe.inputItems(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(GFContent.IDS.item(ref))));
            }
            // Separate stacks respect the food's max stack size of one.
            for (int i = 0; i < source.servings(); i++) recipe.outputItems(GFContent.item(dish.id()));
            recipe.save(out);
        }
    }

    private static void controller(Consumer<FinishedRecipe> out, boolean cooking) {
        var machine = cooking ? GFMachines.COOKING_WORKSHOP : GFMachines.PREP_WORKSHOP;
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, machine.asStack().getItem())
                .pattern("PCP").pattern("MHM").pattern("WGW")
                .define('P', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Steel).getItem())
                .define('C', com.gregtechceu.gtceu.data.recipe.CustomTags.LV_CIRCUITS)
                .define('M', cooking ? GTItems.ELECTRIC_PUMP_LV.get() : GTItems.ELECTRIC_MOTOR_LV.get())
                .define('H', GTMachines.HULL[GTValues.LV].asStack().getItem())
                .define('W', ChemicalHelper.get(TagPrefix.cableGtSingle, GTMaterials.Tin).getItem())
                .define('G', WorkshopPatterns.moduleBlock(cooking))
                .unlockedBy("has_lv_hull", InventoryChangeTrigger.TriggerInstance.hasItems(GTMachines.HULL[GTValues.LV].asStack().getItem()))
                .save(out, GregFoodExpansion.id("controller/" + machine.getName()));
    }
}
