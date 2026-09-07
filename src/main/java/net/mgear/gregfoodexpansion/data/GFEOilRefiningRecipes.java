package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.registry.GTFEMaterials;
import net.minecraft.data.recipes.FinishedRecipe;

/**
 * 回油精炼产线(2026-09-08 提案:回油必须经产线处理才可复用,且有损):
 *
 * <pre>
 * 废食用油 1,000
 *   → 蒸馏室·过滤脱杂(+滤纸)→ 洗涤回油 900(损 10%)
 *   → 化学反应釜·碱炼脱酸(+氢氧化钠粉)→ 碱炼回油 700(损 22%)
 *   → 蒸馏室·脱臭精炼 → 食用油 500(损 29%;合计回收率 50%)
 * </pre>
 *
 * 加上炸锅每批吸收 100-200 mB,油炸用油的全周期净损耗约 55-65%,形成真实的
 * 废油再生工业负担。全部 GTCEu 现有 LV 机型(蒸馏室 ×2 + 化学反应釜 ×1)。
 */
public final class GFEOilRefiningRecipes {
    private GFEOilRefiningRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        // Step 1 过滤脱杂(蒸馏室):滤纸吸附食物残渣,损 10%
        GTRecipeTypes.DISTILLERY_RECIPES
                .recipeBuilder(GregFoodExpansion.id("oil_refining/filter"))
                .inputItems(net.minecraft.world.item.Items.PAPER)
                .inputFluids(GTFEMaterials.USED_COOKING_OIL.getFluid(1000))
                .outputFluids(GTFEMaterials.WASHED_COOKING_OIL.getFluid(900))
                .duration(100)
                .EUt(16)
                .save(provider);

        // Step 2 碱炼脱酸(化学反应釜):氢氧化钠中和游离脂肪酸,生成皂脚弃置,损 22%
        GTRecipeTypes.CHEMICAL_RECIPES
                .recipeBuilder(GregFoodExpansion.id("oil_refining/neutralize"))
                .inputItems(TagPrefix.dust, GTMaterials.SodiumHydroxide)
                .inputFluids(GTFEMaterials.WASHED_COOKING_OIL.getFluid(900))
                .outputFluids(GTFEMaterials.NEUTRALIZED_COOKING_OIL.getFluid(700))
                .duration(160)
                .EUt(16)
                .save(provider);

        // Step 3 脱臭精炼(蒸馏室):高温真空汽提脱色脱臭,损 29%,产出食用油回到炸锅循环
        GTRecipeTypes.DISTILLERY_RECIPES
                .recipeBuilder(GregFoodExpansion.id("oil_refining/deodorize"))
                .inputFluids(GTFEMaterials.NEUTRALIZED_COOKING_OIL.getFluid(700))
                .outputFluids(GTMaterials.SeedOil.getFluid(500))
                .duration(200)
                .EUt(16)
                .save(provider);
    }
}
