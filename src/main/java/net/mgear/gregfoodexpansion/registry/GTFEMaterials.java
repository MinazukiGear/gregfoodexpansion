package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.registry.MaterialRegistry;
import com.gregtechceu.gtceu.common.unification.material.MaterialRegistryManager;
import net.mgear.gregfoodexpansion.GregFoodExpansion;

/**
 * 本模组自有 GTCEu 材料(流体)。废油三段链为油炸回收产线的中间流体:
 * 炸锅输出废食用油 → 精炼产线(GFEOilRefiningRecipes)逐段净化回食用油,
 * 每段有损。材料注册走 GTCEu 附属机制:createRegistry(MOD_ID) + MaterialEvent。
 *
 * 大豆链流体(soybean-chain.md §2,2026-09-08 实现期补充查证):
 * - 发酵用盐水直接复用 GTCEu SaltWater(盐+水,搅拌机既有配方),不自建;
 * - 苦卤(bittern)保持独立材料:语义为氯化镁母液,与 GTCEu SaltWater(盐+水)不同;
 * - 豆油入 #forge:cooking_oil、生抽/老抽入 #forge:soy_sauces(GFEFluidTags)。
 */
public final class GTFEMaterials {
    private GTFEMaterials() {}

    public static MaterialRegistry MATERIAL_REGISTRY;
    public static Material USED_COOKING_OIL;
    public static Material WASHED_COOKING_OIL;
    public static Material NEUTRALIZED_COOKING_OIL;
    // ---- 大豆链(soybean-chain.md §2) ----
    public static Material SOYMILK;
    public static Material SOYBEAN_OIL;
    public static Material SOYBEAN_MASH;
    public static Material SOY_SAUCE;
    public static Material DARK_SOY_SAUCE;
    public static Material CARAMEL_SYRUP;
    public static Material BITTERN;
    // ---- 淀粉糖浆链(starch-chain.md §2,GTCEu 无淀粉/葡萄糖,需自建;m0 §2.3 已证) ----
    public static Material STARCH;
    public static Material GLUCOSE;
    public static Material CORN_SYRUP;
    // ---- 酒线中间流体(alcohol-line.md §1/§2;榨汁/糖水/醪,发酵槽 1/1/1/1 的预混解) ----
    public static Material GRAPE_JUICE;
    public static Material APPLE_JUICE;
    public static Material WORT;
    public static Material SUGAR_MUST;
    public static Material RICE_MASH;
    public static Material HUANGJIU_MASH;
    // ---- 酒线成品流体(alcohol-line.md §2/§3,发酵酒 6 + 蒸馏酒 3) ----
    public static Material WINE;
    public static Material BEER;
    public static Material RICE_WINE;
    public static Material HUANGJIU;
    public static Material CIDER;
    public static Material MEAD;
    public static Material BRANDY;
    public static Material WHISKY;
    public static Material BAIJIU;

    public static void createRegistry(MaterialRegistryEvent event) {
        MATERIAL_REGISTRY = MaterialRegistryManager.getInstance()
                .createRegistry(GregFoodExpansion.MOD_ID);
    }

    public static void register(MaterialEvent event) {
        USED_COOKING_OIL = new Material.Builder(GregFoodExpansion.id("used_cooking_oil"))
                .fluid()
                .color(0x6E5A20)
                .buildAndRegister();
        WASHED_COOKING_OIL = new Material.Builder(GregFoodExpansion.id("washed_cooking_oil"))
                .fluid()
                .color(0x8A7634)
                .buildAndRegister();
        NEUTRALIZED_COOKING_OIL = new Material.Builder(GregFoodExpansion.id("neutralized_cooking_oil"))
                .fluid()
                .color(0xA89448)
                .buildAndRegister();

        SOYMILK = new Material.Builder(GregFoodExpansion.id("soymilk"))
                .fluid()
                .color(0xF5F0E1)
                .buildAndRegister();
        SOYBEAN_OIL = new Material.Builder(GregFoodExpansion.id("soybean_oil"))
                .fluid()
                .color(0xD9C46A)
                .buildAndRegister();
        SOYBEAN_MASH = new Material.Builder(GregFoodExpansion.id("soybean_mash"))
                .fluid()
                .color(0x5A3A22)
                .buildAndRegister();
        SOY_SAUCE = new Material.Builder(GregFoodExpansion.id("soy_sauce"))
                .fluid()
                .color(0x4A2C14)
                .buildAndRegister();
        DARK_SOY_SAUCE = new Material.Builder(GregFoodExpansion.id("dark_soy_sauce"))
                .fluid()
                .color(0x2B1608)
                .buildAndRegister();
        CARAMEL_SYRUP = new Material.Builder(GregFoodExpansion.id("caramel_syrup"))
                .fluid()
                .color(0x8A4A10)
                .buildAndRegister();
        BITTERN = new Material.Builder(GregFoodExpansion.id("bittern"))
                .fluid()
                .color(0xBCC8CC)
                .buildAndRegister();

        // 淀粉:玉米湿磨产物,白色粉末(食品级;千叶豆腐真实原料,M3 味精链起点)
        STARCH = new Material.Builder(GregFoodExpansion.id("starch"))
                .dust()
                .color(0xF8F5EA)
                .buildAndRegister();
        // 葡萄糖:淀粉酸解糖化产物(M1 酸解低出成;M3 酶解高出成),味精链上游
        GLUCOSE = new Material.Builder(GregFoodExpansion.id("glucose"))
                .dust()
                .color(0xFCF9F0)
                .buildAndRegister();
        // 玉米糖浆:淀粉部分水解中间品(食品甜味剂,饮品/烘焙 M2 消费点预留)
        CORN_SYRUP = new Material.Builder(GregFoodExpansion.id("corn_syrup"))
                .fluid()
                .color(0xD8A860)
                .buildAndRegister();

        // ---- 酒线流体(alcohol-line.md,2026-09-08 实装;GT 方块流体按材料色自动出图) ----
        // 中间流体:两汁/麦芽汁/稀糖水(酵母扩繁+蜂蜜酒共用)/两米醪(米酒醪甜型、黄酒醪醇型)
        GRAPE_JUICE = new Material.Builder(GregFoodExpansion.id("grape_juice"))
                .fluid()
                .color(0x7A3560)
                .buildAndRegister();
        APPLE_JUICE = new Material.Builder(GregFoodExpansion.id("apple_juice"))
                .fluid()
                .color(0xE3B84D)
                .buildAndRegister();
        WORT = new Material.Builder(GregFoodExpansion.id("wort"))
                .fluid()
                .color(0xB07C2E)
                .buildAndRegister();
        SUGAR_MUST = new Material.Builder(GregFoodExpansion.id("sugar_must"))
                .fluid()
                .color(0xF2E6B8)
                .buildAndRegister();
        RICE_MASH = new Material.Builder(GregFoodExpansion.id("rice_mash"))
                .fluid()
                .color(0xF3EEDD)
                .buildAndRegister();
        HUANGJIU_MASH = new Material.Builder(GregFoodExpansion.id("huangjiu_mash"))
                .fluid()
                .color(0xC08850)
                .buildAndRegister();

        // 发酵酒 6
        WINE = new Material.Builder(GregFoodExpansion.id("wine"))
                .fluid()
                .color(0x6B2140)
                .buildAndRegister();
        BEER = new Material.Builder(GregFoodExpansion.id("beer"))
                .fluid()
                .color(0xE0A437)
                .buildAndRegister();
        RICE_WINE = new Material.Builder(GregFoodExpansion.id("rice_wine"))
                .fluid()
                .color(0xF7F3E3)
                .buildAndRegister();
        HUANGJIU = new Material.Builder(GregFoodExpansion.id("huangjiu"))
                .fluid()
                .color(0xC98A2E)
                .buildAndRegister();
        CIDER = new Material.Builder(GregFoodExpansion.id("cider"))
                .fluid()
                .color(0xE9C766)
                .buildAndRegister();
        MEAD = new Material.Builder(GregFoodExpansion.id("mead"))
                .fluid()
                .color(0xE2A33F)
                .buildAndRegister();
        // 蒸馏酒 3
        BRANDY = new Material.Builder(GregFoodExpansion.id("brandy"))
                .fluid()
                .color(0xA34B23)
                .buildAndRegister();
        WHISKY = new Material.Builder(GregFoodExpansion.id("whisky"))
                .fluid()
                .color(0x82431C)
                .buildAndRegister();
        BAIJIU = new Material.Builder(GregFoodExpansion.id("baijiu"))
                .fluid()
                .color(0xFAFAF2)
                .buildAndRegister();
    }
}
