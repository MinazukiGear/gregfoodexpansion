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

/**
 * 自建配方类型(机器骨架首版,数值为草案,随 M1' 配方批次收口):
 *
 * <ul>
 *   <li>{@link #PREP_WORKSHOP_RECIPES} 切配工坊(LV):形态配料/香料现磨/压延(品位保真);</li>
 *   <li>{@link #COOKING_WORKSHOP_RECIPES} 烹饪工坊(LV):煮(含巴氏低温档)/蒸/炒/炸;</li>
 *   <li>{@link #TUNNEL_OVEN_RECIPES} 隧道烤炉(MV):连续烘烤,炉温段模块决定配方门槛(强制预热)。</li>
 * </ul>
 *
 * <p>默认 EUt/时长为骨架占位基准,实际配方在数据表 PR 中逐条标注;
 * 预热(隧道烤炉)与段位门槛(结构化模块,machines.md §5.0)在配方逻辑接通时实装。</p>
 */
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
