package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.mgear.gregfoodexpansion.registry.GTFEMaterials;
import net.minecraft.data.recipes.FinishedRecipe;

/**
 * 淀粉糖浆链(starch-chain.md,M1 首个闭环清单项)。
 *
 * <pre>
 * 玉米 ×3 + 水 500
 *   → 搅拌机·湿磨取淀粉 → 淀粉尘 ×4(LV)
 *   ├→ 化学反应釜·糊化液化(淀粉1+水250)→ 玉米糖浆 250 mB(LV,物理无试剂)
 *   └→ 化学反应釜·酸解糖化(淀粉2+水500+盐酸100)→ 葡萄糖 ×2(MV,biochem §2.1 酸解路线)
 * </pre>
 *
 * 范围线(M1):只做湿磨 + 物理液化 + 酸解;**酶解糖化**(糖化酶耐久催化,高出成)
 * 留 M3 随酶制剂家族接入(biochem-chain.md §4),届时替换/并列酸解配方。
 * 玉米油未纳入本链(湿磨副产胚芽榨油留肉品/油脂线扩展,见 starch-chain.md §6)。
 * 葡萄糖 = M3 味精链上游;玉米糖浆 = 饮品/烘焙甜味剂(M2 消费点预留)。
 */
public final class GFEStarchRecipes {
    private GFEStarchRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        // ---- 湿磨取淀粉(搅拌机):浸泡破碎分离,出率 ~70%(玉米×3 → 淀粉×4) ----
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("starch/wet_milling"))
                .inputItems(GFECropItems.CORN.get(), 3)
                .inputFluids(GTMaterials.Water.getFluid(500))
                .outputItems(TagPrefix.dust, GTFEMaterials.STARCH, 4)
                .duration(200)
                .EUt(8)
                .save(provider);

        // ---- 糊化液化(化学反应釜,LV):淀粉遇热糊化部分水解,无试剂,物理步骤 ----
        GTRecipeTypes.CHEMICAL_RECIPES
                .recipeBuilder(GregFoodExpansion.id("starch/corn_syrup"))
                .inputItems(TagPrefix.dust, GTFEMaterials.STARCH)
                .inputFluids(GTMaterials.Water.getFluid(250))
                .outputFluids(GTFEMaterials.CORN_SYRUP.getFluid(250))
                .duration(200)
                .EUt(16)
                .save(provider);

        // ---- 酸解糖化(化学反应釜,MV):盐酸催化完全水解,低出成(淀粉×2 → 葡萄糖×2) ----
        // M3 酶解路线(糖化酶耐久催化)接入时高出成(×3~4),替换/并列本配方由 biochem §2.1 定案。
        GTRecipeTypes.CHEMICAL_RECIPES
                .recipeBuilder(GregFoodExpansion.id("starch/acid_saccharification"))
                .inputItems(TagPrefix.dust, GTFEMaterials.STARCH, 2)
                .inputFluids(GTMaterials.Water.getFluid(500))
                .inputFluids(GTMaterials.HydrochloricAcid.getFluid(100))
                .outputItems(TagPrefix.dust, GTFEMaterials.GLUCOSE, 2)
                .duration(400)
                .EUt(30)
                .save(provider);
    }
}
