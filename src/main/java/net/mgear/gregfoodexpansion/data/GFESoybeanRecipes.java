package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.mgear.gregfoodexpansion.registry.GFERecipeTypes;
import net.mgear.gregfoodexpansion.registry.GTFEMaterials;
import net.mgear.gregfoodexpansion.soybean.GFESoybeanItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 大豆链配方(soybean-chain.md §3,2026-09-08 实现):
 * 零新机器,复用筛选机/搅拌机/发酵槽/蒸馏室/提取机 + 通用烹饪机(蒸/煮)。
 *
 * 实现期机器映射调整(源码查证 GTRecipeTypes.java):
 * - 发酵槽配方 IO 1/1/1/1(:272),"煮大豆+麸皮+种曲"三原料塞不下 →
 *   新增拌曲步(搅拌机:煮大豆+麸皮+种曲→曲料),发酵槽跑"曲料→曲";
 * - 压榨段"发酵醪→生抽"改挂蒸馏室(1/1/1/1,流体进出+物品副产)——
 *   提取机无流体输入槽(1/1/0/1,:258),发酵醪为流体进不去;
 * - 盐水独立成流体(§10-5 收口):发酵段物品槽只能装曲,水+盐无法混配。
 *
 * 数值为首版基准(soybean-chain.md §7 全部待定,实机平衡后微调):
 * 发酵段为全模组长档(发酵 1200t),制曲自然档超长(1200t)低速,快档中短(400t)。
 * 经 IGTAddon#addRecipes 走 GTCEu 动态数据包注册。
 */
public final class GFESoybeanRecipes {
    private GFESoybeanRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        // ---- 筛分:小麦 → 精制面粉(GT 小麦粉) + 麸皮(副产,30%) ----
        GTRecipeTypes.SIFTER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/wheat_sifting"))
                .inputItems(Items.WHEAT)
                .outputItems(TagPrefix.dust, GTMaterials.Wheat)
                .chancedOutput(new ItemStack(GFESoybeanItems.BRAN.get()), 3000, 500)
                .duration(100)
                .EUt(16)
                .save(provider);

        // ---- 磨浆:大豆 ×2 + 水 → 豆浆 250 + 豆渣(副产) ----
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/soymilk"))
                .inputItems(GFECropItems.SOYBEAN.get(), 2)
                .inputFluids(GTMaterials.Water.getFluid(250))
                .outputFluids(GTFEMaterials.SOYMILK.getFluid(250))
                .outputItems(GFESoybeanItems.OKARA.get())
                .duration(200)
                .EUt(8)
                .save(provider);

        // ---- 蒸煮:大豆 + 水 → 煮大豆(通用烹饪机 c2 蒸) ----
        cook(provider, 2, "cooked_soybean", b -> b
                .inputItems(GFECropItems.SOYBEAN.get())
                .inputFluids(GTMaterials.Water.getFluid(50))
                .outputItems(GFESoybeanItems.COOKED_SOYBEAN.get()));

        // ---- 苦卤:氯化镁 + 水(soybean-chain.md §5,M0 查证氯化镁存在) ----
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/bittern"))
                .inputItems(TagPrefix.dust, GTMaterials.MagnesiumChloride)
                .inputFluids(GTMaterials.Water.getFluid(900))
                .outputFluids(GTFEMaterials.BITTERN.getFluid(1000))
                .duration(100)
                .EUt(8)
                .save(provider);

        // ---- 拌曲:煮大豆 + 麸皮 + 种曲 → 曲料 ×2(搅拌机预混,接入种曲) ----
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/koji_batch"))
                .inputItems(GFESoybeanItems.COOKED_SOYBEAN.get())
                .inputItems(GFESoybeanItems.BRAN.get())
                .inputItems(GFESoybeanItems.KOJI_STARTER.get())
                .outputItems(GFESoybeanItems.KOJI_BATCH.get(), 2)
                .duration(200)
                .EUt(8)
                .save(provider);

        // ---- 制曲(自然):煮大豆 → 曲 ×1,超长低速,首次启动用(慢工艺招牌) ----
        GTRecipeTypes.FERMENTING_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/koji_natural"))
                .inputItems(GFESoybeanItems.COOKED_SOYBEAN.get())
                .outputItems(GFESoybeanItems.KOJI.get())
                .duration(1200)
                .EUt(8)
                .save(provider);

        // ---- 制曲(快):曲料 → 曲 ×2(种曲已拌入,接种发酵) ----
        GTRecipeTypes.FERMENTING_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/koji_fast"))
                .inputItems(GFESoybeanItems.KOJI_BATCH.get())
                .outputItems(GFESoybeanItems.KOJI.get(), 2)
                .duration(400)
                .EUt(8)
                .save(provider);

        // ---- 接种:曲 → 种曲 ×4(自举闭环:曲→种曲→拌曲,种曲消耗由 ×4 倍增覆盖) ----
        GTRecipeTypes.FERMENTING_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/koji_starter"))
                .inputItems(GFESoybeanItems.KOJI.get())
                .outputItems(GFESoybeanItems.KOJI_STARTER.get(), 4)
                .duration(300)
                .EUt(8)
                .save(provider);

        // ---- 发酵:曲 ×2 + 盐水(GTCEu SaltWater) → 发酵醪(全模组长档,慢工艺核心) ----
        GTRecipeTypes.FERMENTING_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/soybean_mash"))
                .inputItems(GFESoybeanItems.KOJI.get(), 2)
                .inputFluids(GTMaterials.SaltWater.getFluid(500))
                .outputFluids(GTFEMaterials.SOYBEAN_MASH.getFluid(500))
                .duration(1200)
                .EUt(16)
                .save(provider);

        // ---- 压榨:发酵醪 → 生抽 + 酱渣(蒸馏室;提取机无流体输入槽) ----
        GTRecipeTypes.DISTILLERY_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/soy_sauce_press"))
                .inputFluids(GTFEMaterials.SOYBEAN_MASH.getFluid(1000))
                .outputFluids(GTFEMaterials.SOY_SAUCE.getFluid(800))
                .outputItems(GFESoybeanItems.SOYBEAN_POMACE.get())
                .duration(200)
                .EUt(16)
                .save(provider);

        // ---- 老抽调制:生抽 + 焦糖糖色(红烧类硬性引用老抽本体,soybean-chain.md §4) ----
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/dark_soy_sauce"))
                .inputFluids(GTFEMaterials.SOY_SAUCE.getFluid(900))
                .inputFluids(GTFEMaterials.CARAMEL_SYRUP.getFluid(100))
                .outputFluids(GTFEMaterials.DARK_SOY_SAUCE.getFluid(1000))
                .duration(100)
                .EUt(8)
                .save(provider);

        // ---- 榨油:大豆 ×2 → 豆油 + 豆粕(副产;豆油为 M1 食用油源) ----
        GTRecipeTypes.EXTRACTOR_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/soybean_oil"))
                .inputItems(GFECropItems.SOYBEAN.get(), 2)
                .outputFluids(GTFEMaterials.SOYBEAN_OIL.getFluid(60))
                .outputItems(GFESoybeanItems.SOYBEAN_MEAL.get())
                .duration(200)
                .EUt(16)
                .save(provider);

        // ---- 焦糖糖色:糖 + 水(老抽原料,饮品/烘焙线共享) ----
        cook(provider, 1, "caramel_syrup", b -> b
                .inputItems(Items.SUGAR, 2)
                .inputFluids(GTMaterials.Water.getFluid(100))
                .outputFluids(GTFEMaterials.CARAMEL_SYRUP.getFluid(100)));

        // ---- 点卤(凝固剂为消耗品,soybean-chain.md §5):凝固剂种类决定豆腐品类 ----
        // 北豆腐(质紧,炒/炖):苦卤 50 mB
        cook(provider, 1, "firm_tofu", b -> b
                .inputFluids(GTFEMaterials.SOYMILK.getFluid(250))
                .inputFluids(GTFEMaterials.BITTERN.getFluid(50))
                .outputItems(GFESoybeanItems.FIRM_TOFU.get(), 2));
        // 南豆腐(质嫩,汤/蒸):石膏(硫酸钙粉尘)
        cook(provider, 1, "soft_tofu", b -> b
                .inputFluids(GTFEMaterials.SOYMILK.getFluid(250))
                .inputItems(TagPrefix.dust, GTMaterials.Gypsum)
                .outputItems(GFESoybeanItems.SOFT_TOFU.get(), 2));
        // 豆腐脑(基础档早餐):少量苦卤
        cook(provider, 1, "tofu_pudding", b -> b
                .inputFluids(GTFEMaterials.SOYMILK.getFluid(250))
                .inputFluids(GTFEMaterials.BITTERN.getFluid(10))
                .outputItems(GFESoybeanItems.TOFU_PUDDING.get(), 2));

        // ---- 千叶豆腐:豆浆+淀粉混配成坯,真空冷冻定型(soybean-chain.md §5) ----
        // 真实工艺为大豆蛋白+淀粉;2026-09-08 淀粉糖浆链落地,面粉代用已切换回淀粉(starch-chain.md)。
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/qianye_tofu_blank"))
                .inputFluids(GTFEMaterials.SOYMILK.getFluid(250))
                .inputItems(TagPrefix.dust, GTFEMaterials.STARCH)
                .outputItems(GFESoybeanItems.QIANYE_TOFU_BLANK.get(), 2)
                .duration(200)
                .EUt(8)
                .save(provider);
        GTRecipeTypes.VACUUM_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/qianye_tofu"))
                .inputItems(GFESoybeanItems.QIANYE_TOFU_BLANK.get())
                .outputItems(GFESoybeanItems.QIANYE_TOFU.get(), 2)
                .duration(200)
                .EUt(16)
                .save(provider);

        // ---- 豆干:北豆腐压制脱水(压缩机,2026-09-08 追加) ----
        GTRecipeTypes.COMPRESSOR_RECIPES
                .recipeBuilder(GregFoodExpansion.id("soybean/dried_tofu"))
                .inputItems(GFESoybeanItems.FIRM_TOFU.get(), 2)
                .outputItems(GFESoybeanItems.DRIED_TOFU.get())
                .duration(200)
                .EUt(8)
                .save(provider);

        // ---- 千张:豆浆点卤成浆(通用烹饪机 c1)→ 压延成薄层(切配 c7) ----
        cook(provider, 1, "tofu_sheet_blank", b -> b
                .inputFluids(GTFEMaterials.SOYMILK.getFluid(250))
                .inputFluids(GTFEMaterials.BITTERN.getFluid(30))
                .outputItems(GFESoybeanItems.TOFU_SHEET_BLANK.get()));
    }

    // 通用烹饪机点卤/焦糖走 c1 煮、蒸煮走 c2 蒸;200t / 12 EUt 基准。
    private static void cook(Consumer<FinishedRecipe> provider, int circuit, String name,
                             Consumer<GTRecipeBuilder> config) {
        GTRecipeBuilder builder = GFERecipeTypes.COOKING
                .recipeBuilder(GregFoodExpansion.id("cooking/soybean/" + name))
                .circuitMeta(circuit)
                .duration(200)
                .EUt(12);
        config.accept(builder);
        builder.save(provider);
    }
}
