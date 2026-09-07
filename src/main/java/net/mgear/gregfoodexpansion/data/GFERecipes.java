package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.prep.GFEFormItems;
import net.mgear.gregfoodexpansion.prep.GFEPrepTools;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public final class GFERecipes {
    private GFERecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        addToolRecipes(provider);
    }

    // 手工切配工具(food-processor.md §7):工作台 + 铁质,配方刻意少;
    // 切配机 LV 起接管量产,工具定位为前置过渡。
    private static void addToolRecipes(Consumer<FinishedRecipe> provider) {
        // 菜刀:铁锭 ×2 + 木棍(宽刃 + 柄)
        VanillaRecipeHelper.addShapedRecipe(provider, id("cleaver"),
                new net.minecraft.world.item.ItemStack(GFEPrepTools.CLEAVER.get()),
                "II", "IS",
                'I', Items.IRON_INGOT, 'S', Items.STICK);
        // 削皮刀:铁锭 + 木棍
        VanillaRecipeHelper.addShapedRecipe(provider, id("peeler"),
                new net.minecraft.world.item.ItemStack(GFEPrepTools.PEELER.get()),
                "I", "S",
                'I', Items.IRON_INGOT, 'S', Items.STICK);
        // 研钵:圆石碗形
        VanillaRecipeHelper.addShapedRecipe(provider, id("mortar_pestle"),
                new net.minecraft.world.item.ItemStack(GFEPrepTools.MORTAR_PESTLE.get()),
                "C C", "CCC",
                'C', Items.COBBLESTONE);
        // 擀面杖:木棍 ×2
        VanillaRecipeHelper.addShapedRecipe(provider, id("rolling_pin"),
                new net.minecraft.world.item.ItemStack(GFEPrepTools.ROLLING_PIN.get()),
                "S", "S",
                'S', Items.STICK);

        // 米制品链手工路线(compatibility-boundary Q1:LV 前食品加工仅手搓):
        // 研钵 + 水稻 → 大米粉(研钵覆盖研磨);大米粉 + 水桶 → 米粉团
        VanillaRecipeHelper.addShapelessRecipe(provider, id("rice_flour_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.RICE_FLOUR.get()),
                GFEPrepTools.MORTAR_PESTLE.get(), GFECropItems.RICE.get());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("rice_dough_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.RICE_DOUGH.get()),
                GFEFormItems.RICE_FLOUR.get(), Items.WATER_BUCKET);
    }

    private static ResourceLocation id(String path) {
        return GregFoodExpansion.id(path);
    }
}
