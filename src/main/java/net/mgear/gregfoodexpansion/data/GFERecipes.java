package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTItems;
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
        // 菜刀:铁板 ×5 + 木棍(2026-09-08:用铁配方统一改铁板;提价口径不变)
        VanillaRecipeHelper.addShapedRecipe(provider, id("cleaver"),
                new net.minecraft.world.item.ItemStack(GFEPrepTools.CLEAVER.get()),
                "III", "II ", "S  ",
                'I', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Iron), 'S', Items.STICK);
        // 削皮刀:铁板 ×2 + 木棍
        VanillaRecipeHelper.addShapedRecipe(provider, id("peeler"),
                new net.minecraft.world.item.ItemStack(GFEPrepTools.PEELER.get()),
                "II", " S",
                'I', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Iron), 'S', Items.STICK);
        // 研钵:圆石 ×8
        VanillaRecipeHelper.addShapedRecipe(provider, id("mortar_pestle"),
                new net.minecraft.world.item.ItemStack(GFEPrepTools.MORTAR_PESTLE.get()),
                "CCC", "C C", "CCC",
                'C', Items.COBBLESTONE);
        // 擀面杖:木板 ×4 + 木棍
        VanillaRecipeHelper.addShapedRecipe(provider, id("rolling_pin"),
                new net.minecraft.world.item.ItemStack(GFEPrepTools.ROLLING_PIN.get()),
                "PP", "PP", " S",
                'P', Items.OAK_PLANKS, 'S', Items.STICK);

        // 米制品链手工路线(compatibility-boundary Q1:LV 前食品加工仅手搓):
        // 研钵 + 水稻 → 大米粉(研钵覆盖研磨);大米粉 + 水桶 → 米粉团
        // 擀面杖手工成型(切配机压延的对应手工路线):不同投入量成型不同生坯
        VanillaRecipeHelper.addShapelessRecipe(provider, id("dough_sheet_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.DOUGH_SHEET.get()),
                GFEPrepTools.ROLLING_PIN.get(), GTItems.DOUGH);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("raw_toast_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.RAW_TOAST.get()),
                GFEPrepTools.ROLLING_PIN.get(), GTItems.DOUGH, GTItems.DOUGH);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("raw_bread_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.RAW_BREAD.get()),
                GFEPrepTools.ROLLING_PIN.get(), GTItems.DOUGH, GTItems.DOUGH, GTItems.DOUGH);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("raw_baguette_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.RAW_BAGUETTE.get()),
                GFEPrepTools.ROLLING_PIN.get(), GTItems.DOUGH, GTItems.DOUGH, GTItems.DOUGH, GTItems.DOUGH);

        // 菜刀手切烘焙切分(切配机器的对应手工路线)
        VanillaRecipeHelper.addShapelessRecipe(provider, id("bread_slice_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.BREAD_SLICE.get()),
                GFEPrepTools.CLEAVER.get(), GFEDishes.BREAD.get());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("baguette_slice_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.BAGUETTE_SLICE.get()),
                GFEPrepTools.CLEAVER.get(), GFEDishes.BAGUETTE.get());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("burger_bun_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.BURGER_BUN.get()),
                GFEPrepTools.CLEAVER.get(), GFEDishes.DINNER_ROLL.get());

        // 面皮切丝成面条(手工:菜刀)
        VanillaRecipeHelper.addShapelessRecipe(provider, id("noodle_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.NOODLE.get()),
                GFEPrepTools.CLEAVER.get(), GFEFormItems.DOUGH_SHEET.get());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("rice_flour_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.RICE_FLOUR.get()),
                GFEPrepTools.MORTAR_PESTLE.get(), GFECropItems.RICE.get());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("rice_dough_manual"),
                new net.minecraft.world.item.ItemStack(GFEFormItems.RICE_DOUGH.get()),
                GFEFormItems.RICE_FLOUR.get(), Items.WATER_BUCKET);
    }

    // 手工烹饪工具(universal-cooker.md §7):厨刀/炒锅/蒸笼,铁系+竹木,炸无手工路径
    private static void addCookingToolRecipes(Consumer<FinishedRecipe> provider) {
        // 厨刀:铁板 ×3 + 木棍(与菜刀形状区分)
        VanillaRecipeHelper.addShapedRecipe(provider, id("kitchen_knife"),
                new net.minecraft.world.item.ItemStack(GFECookingTools.KITCHEN_KNIFE.get()),
                "II", "I ", "S ",
                'I', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Iron), 'S', Items.STICK);
        // 炒锅:铁板 ×6(碗体)+ 铁杆 ×2(双耳/双足)
        VanillaRecipeHelper.addShapedRecipe(provider, id("wok"),
                new net.minecraft.world.item.ItemStack(GFECookingTools.WOK.get()),
                "III", "III", "R R",
                'I', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Iron),
                'R', ChemicalHelper.get(TagPrefix.rod, GTMaterials.Iron));
        // 蒸笼:竹 ×5(笼格,2026-09-08 降本易获得)
        VanillaRecipeHelper.addShapedRecipe(provider, id("steamer"),
                new net.minecraft.world.item.ItemStack(GFECookingTools.STEAMER.get()),
                "BBB", "B B",
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
