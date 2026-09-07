package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.cooking.GFECookingTools;
import net.mgear.gregfoodexpansion.cooking.GFEDishes;
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
        addCookingToolRecipes(provider);
        addHandCookingRecipes(provider);
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

    // 手工烹饪工具(universal-cooker.md §7):厨刀/炒锅/蒸笼,铁系+竹木,炸无手工路径
    private static void addCookingToolRecipes(Consumer<FinishedRecipe> provider) {
        // 厨刀:铁锭 ×2 + 木棍(竖版窄刃,与菜刀/削皮刀形状区分)
        VanillaRecipeHelper.addShapedRecipe(provider, id("kitchen_knife"),
                new net.minecraft.world.item.ItemStack(GFECookingTools.KITCHEN_KNIFE.get()),
                "I", "I", "S",
                'I', Items.IRON_INGOT, 'S', Items.STICK);
        // 炒锅:铁锭 ×5(碗形)
        VanillaRecipeHelper.addShapedRecipe(provider, id("wok"),
                new net.minecraft.world.item.ItemStack(GFECookingTools.WOK.get()),
                "I I", "III",
                'I', Items.IRON_INGOT);
        // 蒸笼:竹 ×7(双层笼格)
        VanillaRecipeHelper.addShapedRecipe(provider, id("steamer"),
                new net.minecraft.world.item.ItemStack(GFECookingTools.STEAMER.get()),
                "BBB", "B B", "BBB",
                'B', Items.BAMBOO);
    }

    // 基础档菜肴(dishes-and-gains.md §5 手工名录,M1 原料可达 8 道;全部急迫 I 15 s)
    private static void addHandCookingRecipes(Consumer<FinishedRecipe> provider) {
        // 厨刀
        VanillaRecipeHelper.addShapelessRecipe(provider, id("fruit_platter"),
                new net.minecraft.world.item.ItemStack(GFEDishes.FRUIT_PLATTER.get()),
                GFECookingTools.KITCHEN_KNIFE.get(), Items.APPLE, GFECropItems.GRAPE.get(), Items.MELON_SLICE);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("sugar_tomato"),
                new net.minecraft.world.item.ItemStack(GFEDishes.SUGAR_TOMATO.get()),
                GFECookingTools.KITCHEN_KNIFE.get(), GFECropItems.TOMATO.get(), Items.SUGAR);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("chicken_cold_noodles"),
                new net.minecraft.world.item.ItemStack(GFEDishes.CHICKEN_COLD_NOODLES.get()),
                GFECookingTools.KITCHEN_KNIFE.get(), GFEFormItems.NOODLE.get(),
                GFEFormItems.CHICKEN_SHRED.get());
        // 炒锅
        VanillaRecipeHelper.addShapelessRecipe(provider, id("fried_egg"),
                new net.minecraft.world.item.ItemStack(GFEDishes.FRIED_EGG.get()),
                GFECookingTools.WOK.get(), Items.EGG);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("plain_noodles"),
                new net.minecraft.world.item.ItemStack(GFEDishes.PLAIN_NOODLES.get()),
                GFECookingTools.WOK.get(), GFEFormItems.NOODLE.get(), Items.WATER_BUCKET);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("hand_fried_rice"),
                new net.minecraft.world.item.ItemStack(GFEDishes.HAND_FRIED_RICE.get()),
                GFECookingTools.WOK.get(), GFECropItems.RICE.get(), Items.EGG);
        // 蒸笼
        VanillaRecipeHelper.addShapelessRecipe(provider, id("hand_steamed_egg"),
                new net.minecraft.world.item.ItemStack(GFEDishes.HAND_STEAMED_EGG.get()),
                GFECookingTools.STEAMER.get(), Items.EGG, Items.EGG);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("hand_steamed_corn"),
                new net.minecraft.world.item.ItemStack(GFEDishes.HAND_STEAMED_CORN.get()),
                GFECookingTools.STEAMER.get(), GFECropItems.CORN.get());
    }

    private static ResourceLocation id(String path) {
        return GregFoodExpansion.id(path);
    }
}
