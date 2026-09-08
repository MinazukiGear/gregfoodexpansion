package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.alcohol.GFEAlcoholItems;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.mgear.gregfoodexpansion.registry.GTFEMaterials;
import net.mgear.gregfoodexpansion.soybean.GFESoybeanItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

/**
 * 酒线配方(alcohol-line.md,2026-09-08 定案实装;零新机器,全部复用既有类型)。
 *
 * 发酵槽 IO 1/1/1/1 的约束下,"多原料预混成醪"是全链骨架(与大豆链拌曲同款解法):
 * 榨汁走提取机(1 物品进/流体出),谷物/糖水醪走搅拌机(多物品进)。
 * 酵母以物品投入发酵槽占满物品槽 → 醪先行;米酒/黄酒用曲自带菌系,不外加酵母。
 *
 * 数值为首版基准(实机平衡后微调):发酵 400-1200t,蒸馏 200-240t,灌装 100t。
 * 米酒(甜型,米×2,400t)/黄酒(醇型,米×4,1200t)共用醪型思路但分醪分产品线(用户定案)。
 * 大麦→麦芽走原版熔炉(GFERecipes.addMaltRecipes),GT 电炉自动兼容原版烟熏配方。
 */
public final class GFEAlcoholRecipes {
    private GFEAlcoholRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        premix(provider);
        yeast(provider);
        ferment(provider);
        distill(provider);
        bottle(provider);
    }

    // ---- 预混段:榨汁(提取机)+ 醪/糖水(搅拌机) ----
    private static void premix(Consumer<FinishedRecipe> provider) {
        // 榨汁:葡萄/苹果 ×2 → 汁 250(提取机,纯榨汁无水相)
        GTRecipeTypes.EXTRACTOR_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/grape_juice"))
                .inputItems(GFECropItems.GRAPE.get(), 2)
                .outputFluids(GTFEMaterials.GRAPE_JUICE.getFluid(250))
                .duration(200)
                .EUt(16)
                .save(provider);
        GTRecipeTypes.EXTRACTOR_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/apple_juice"))
                .inputItems(net.minecraft.world.item.Items.APPLE, 2)
                .outputFluids(GTFEMaterials.APPLE_JUICE.getFluid(250))
                .duration(200)
                .EUt(16)
                .save(provider);

        // 稀糖水:糖 ×2 + 水 250(酵母扩繁 Y2 与蜂蜜酒共用)
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/sugar_must"))
                .inputItems(Items.SUGAR, 2)
                .inputFluids(GTMaterials.Water.getFluid(250))
                .outputFluids(GTFEMaterials.SUGAR_MUST.getFluid(250))
                .duration(200)
                .EUt(8)
                .save(provider);

        // 麦芽汁:麦芽 ×2 + 啤酒花 ×1 + 水 250(酒花入醪同煮,风味必需投入)
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/wort"))
                .inputItems(GFEAlcoholItems.MALT.get(), 2)
                .inputItems(GFECropItems.HOPS.get())
                .inputFluids(GTMaterials.Water.getFluid(250))
                .outputFluids(GTFEMaterials.WORT.getFluid(250))
                .duration(200)
                .EUt(8)
                .save(provider);

        // 发酵面团:GT 面团 + 酵母 → 发酵面团(酵母接入烘焙,M1"烘焙工业化(酵母)"销账;
        // 发酵槽 1/1/1/1 装不下面团+酵母双物品,和面掺酵母走搅拌机;手工路线见 GFERecipes)
        // 工业发酵:死面 ×6 + 酵母 ×1 → 发酵面团 ×6(与手工同口径按批计,600t 线性于批量)
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/leavened_dough"))
                .inputItems(com.gregtechceu.gtceu.common.data.GTItems.DOUGH, 6)
                .inputItems(GFEAlcoholItems.YEAST.get())
                .outputItems(net.mgear.gregfoodexpansion.prep.GFEFormItems.LEAVENED_DOUGH.get(), 6)
                .duration(600)
                .EUt(8)
                .save(provider);

        // 米酒醪(甜型):米 ×2 + 曲 ×1 + 水 250 —— c1
        // 黄酒醪(醇型):米 ×4 + 曲 ×1 + 水 250 —— c2
        // 两条醪输入互为超集,编程电路区分(GTCEu 单方块机 GUI 均有电路槽)。
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/rice_mash"))
                .circuitMeta(1)
                .inputItems(GFECropItems.RICE.get(), 2)
                .inputItems(GFESoybeanItems.KOJI.get())
                .inputFluids(GTMaterials.Water.getFluid(250))
                .outputFluids(GTFEMaterials.RICE_MASH.getFluid(250))
                .duration(200)
                .EUt(8)
                .save(provider);
        GTRecipeTypes.MIXER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/huangjiu_mash"))
                .circuitMeta(2)
                .inputItems(GFECropItems.RICE.get(), 4)
                .inputItems(GFESoybeanItems.KOJI.get())
                .inputFluids(GTMaterials.Water.getFluid(250))
                .outputFluids(GTFEMaterials.HUANGJIU_MASH.getFluid(250))
                .duration(200)
                .EUt(8)
                .save(provider);
    }

    // ---- 酵母自举(alcohol-line.md §1,全酒闸门):葡萄皮表野生酵母起步,糖水扩繁 ----
    private static void yeast(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.FERMENTING_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/yeast_bootstrap"))
                .inputItems(GFECropItems.GRAPE.get(), 4)
                .inputFluids(GTMaterials.Water.getFluid(100))
                .outputItems(GFEAlcoholItems.YEAST.get(), 2)
                .duration(600)
                .EUt(16)
                .save(provider);
        GTRecipeTypes.FERMENTING_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/yeast_propagate"))
                .inputItems(GFEAlcoholItems.YEAST.get())
                .inputFluids(GTFEMaterials.SUGAR_MUST.getFluid(100))
                .outputItems(GFEAlcoholItems.YEAST.get(), 4)
                .duration(200)
                .EUt(16)
                .save(provider);
    }

    // ---- 发酵段:发酵槽 1 物品 + 1 流体,醪/汁 + 酵母(米酒/黄酒曲系自带免酵母) ----
    private static void ferment(Consumer<FinishedRecipe> provider) {
        fermenting(provider, "wine", b -> b
                .inputItems(GFEAlcoholItems.YEAST.get())
                .inputFluids(GTFEMaterials.GRAPE_JUICE.getFluid(250))
                .outputFluids(GTFEMaterials.WINE.getFluid(250))
                .duration(1200));
        fermenting(provider, "beer", b -> b
                .inputItems(GFEAlcoholItems.YEAST.get())
                .inputFluids(GTFEMaterials.WORT.getFluid(250))
                .outputFluids(GTFEMaterials.BEER.getFluid(250))
                .duration(1200));
        fermenting(provider, "cider", b -> b
                .inputItems(GFEAlcoholItems.YEAST.get())
                .inputFluids(GTFEMaterials.APPLE_JUICE.getFluid(250))
                .outputFluids(GTFEMaterials.CIDER.getFluid(250))
                .duration(1000));
        fermenting(provider, "mead", b -> b
                .inputItems(GFEAlcoholItems.YEAST.get())
                .inputFluids(GTFEMaterials.SUGAR_MUST.getFluid(250))
                .outputFluids(GTFEMaterials.MEAD.getFluid(250))
                .duration(1000));
        fermenting(provider, "rice_wine", b -> b
                .inputFluids(GTFEMaterials.RICE_MASH.getFluid(250))
                .outputFluids(GTFEMaterials.RICE_WINE.getFluid(250))
                .duration(400));
        fermenting(provider, "huangjiu", b -> b
                .inputFluids(GTFEMaterials.HUANGJIU_MASH.getFluid(250))
                .outputFluids(GTFEMaterials.HUANGJIU.getFluid(250))
                .duration(1200));
    }

    private static void fermenting(Consumer<FinishedRecipe> provider, String name,
                                   Consumer<com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder> config) {
        com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder builder = GTRecipeTypes.FERMENTING_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/" + name))
                .EUt(16);
        config.accept(builder);
        builder.save(provider);
    }

    // ---- 蒸馏段(蒸馏室,流体→流体,复用大豆链压榨同类型):浓缩增值 ×0.5 ----
    private static void distill(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.DISTILLERY_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/brandy"))
                .inputFluids(GTFEMaterials.WINE.getFluid(500))
                .outputFluids(GTFEMaterials.BRANDY.getFluid(250))
                .duration(200)
                .EUt(30)
                .save(provider);
        GTRecipeTypes.DISTILLERY_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/whisky"))
                .inputFluids(GTFEMaterials.BEER.getFluid(500))
                .outputFluids(GTFEMaterials.WHISKY.getFluid(250))
                .duration(200)
                .EUt(30)
                .save(provider);
        GTRecipeTypes.DISTILLERY_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/baijiu"))
                .inputFluids(GTFEMaterials.HUANGJIU.getFluid(500))
                .outputFluids(GTFEMaterials.BAIJIU.getFluid(250))
                .duration(240)
                .EUt(64)
                .save(provider);
    }

    // ---- 装瓶段(GTCEu 灌装机 CANNER 2/2/1/1,alcohol-line.md §5 定案):
    // 单瓶 1 瓶 + 250mB,批量 4 瓶 + 1000mB;酒效果见 GFEAlcoholItems §4 分档 ----
    private static void bottle(Consumer<FinishedRecipe> provider) {
        bottleOne(provider, "beer", GTFEMaterials.BEER, GFEAlcoholItems.BOTTLED_BEER);
        bottleOne(provider, "cider", GTFEMaterials.CIDER, GFEAlcoholItems.BOTTLED_CIDER);
        bottleOne(provider, "mead", GTFEMaterials.MEAD, GFEAlcoholItems.BOTTLED_MEAD);
        bottleOne(provider, "rice_wine", GTFEMaterials.RICE_WINE, GFEAlcoholItems.BOTTLED_RICE_WINE);
        bottleOne(provider, "wine", GTFEMaterials.WINE, GFEAlcoholItems.BOTTLED_WINE);
        bottleOne(provider, "huangjiu", GTFEMaterials.HUANGJIU, GFEAlcoholItems.BOTTLED_HUANGJIU);
        bottleOne(provider, "brandy", GTFEMaterials.BRANDY, GFEAlcoholItems.BOTTLED_BRANDY);
        bottleOne(provider, "whisky", GTFEMaterials.WHISKY, GFEAlcoholItems.BOTTLED_WHISKY);
        bottleOne(provider, "baijiu", GTFEMaterials.BAIJIU, GFEAlcoholItems.BOTTLED_BAIJIU);
    }

    /** 单瓶 + 批量两档。 */
    private static void bottleOne(Consumer<FinishedRecipe> provider, String name,
                                  com.gregtechceu.gtceu.api.data.chemical.material.Material fluid,
                                  net.minecraftforge.registries.RegistryObject<net.minecraft.world.item.Item> bottle) {
        GTRecipeTypes.CANNER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/bottle_" + name))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(fluid.getFluid(250))
                .outputItems(bottle.get())
                .duration(100)
                .EUt(8)
                .save(provider);
        GTRecipeTypes.CANNER_RECIPES
                .recipeBuilder(GregFoodExpansion.id("alcohol/bottle_" + name + "_bulk"))
                .inputItems(Items.GLASS_BOTTLE, 4)
                .inputFluids(fluid.getFluid(1000))
                .outputItems(bottle.get(), 4)
                .duration(200)
                .EUt(8)
                .save(provider);
    }
}
