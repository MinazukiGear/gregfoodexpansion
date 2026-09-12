package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.mgear.gregfoodexpansion.GregFoodExpansion;

/** Recipe types for structural LV processing and the reserved MV tunnel oven. */
public final class GFRecipeTypes {
    public static GTRecipeType PREP_WORKSHOP_RECIPES;
    public static GTRecipeType COOKING_WORKSHOP_RECIPES;
    public static GTRecipeType TUNNEL_OVEN_RECIPES;

    private GFRecipeTypes() {}

    public static void init(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        PREP_WORKSHOP_RECIPES = register("prep_workshop",
                builder -> builder.duration(100).EUt(30), event);
        COOKING_WORKSHOP_RECIPES = register("cooking_workshop",
                builder -> builder.duration(100).EUt(30), event);
        COOKING_WORKSHOP_RECIPES.setMaxIOSize(6, 4, 1, 1);
        PREP_WORKSHOP_RECIPES.addDataInfo(data -> net.minecraft.network.chat.Component.translatable(
                "gregfoodexpansion.workshop.requires", net.minecraft.network.chat.Component.translatable(
                        "gregfoodexpansion.module." + data.getString("gfe_module"))).getString());
        COOKING_WORKSHOP_RECIPES.addDataInfo(data -> net.minecraft.network.chat.Component.translatable(
                "gregfoodexpansion.workshop.requires", net.minecraft.network.chat.Component.translatable(
                        "gregfoodexpansion.module." + data.getString("gfe_module"))).getString());
        TUNNEL_OVEN_RECIPES = register("tunnel_oven",
                builder -> builder.duration(200).EUt(120), event);
    }

    private static GTRecipeType register(String name,
                                         java.util.function.Consumer<com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder> prepareBuilder,
                                         GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        ResourceLocation id = GregFoodExpansion.id(name);
        GTRecipeType recipeType = new GTRecipeType(id, GTRecipeTypes.MULTIBLOCK)
                .setMaxIOSize(2, 4, 1, 1)
                .setEUIO(IO.IN)
                .prepareBuilder(prepareBuilder);

        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName,
                new GTRecipeSerializer());
        event.register(id, recipeType);
        return recipeType;
    }
}
