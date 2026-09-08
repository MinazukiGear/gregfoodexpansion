package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.cooking.GFEDishes;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.minecraft.data.recipes.FinishedRecipe;

/**
 * 发酵槽食品配方(FERMENTING maxIO 1/1/1/1,GTRecipeTypes.java:272 已核对)。
 * M0 查证:GTCEu 7.5.3 发酵槽仅 1 条生物质配方,食品发酵配方空间近乎空白——
 * M1 最优先复用点。后续酸奶/葡萄酒/啤酒等 M1 剩余项也落在本文件。
 *
 * 泡菜(2026-09-08,M1 首个闭环清单项):白菜 + 盐水低温发酵,产率 ×2。
 * 精制档小菜(急迫 I 5 min,无副效果);兼未来配料(泡菜炒饭等后补)。
 */
public final class GFEFermentingRecipes {
    private GFEFermentingRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        // ---- 泡菜:白菜 ×1 + 盐水 100 mB → 泡菜 ×2(LV,400 tick 长发酵) ----
        GTRecipeTypes.FERMENTING_RECIPES
                .recipeBuilder(GregFoodExpansion.id("fermenting/pickled_cabbage"))
                .inputItems(GFECropItems.CABBAGE.get())
                .inputFluids(GTMaterials.SaltWater.getFluid(100))
                .outputItems(GFEDishes.PICKLED_CABBAGE.get(), 2)
                .duration(400)
                .EUt(8)
                .save(provider);
    }
}
