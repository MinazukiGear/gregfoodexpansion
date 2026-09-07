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
 */
public final class GTFEMaterials {
    private GTFEMaterials() {}

    public static MaterialRegistry MATERIAL_REGISTRY;
    public static Material USED_COOKING_OIL;
    public static Material WASHED_COOKING_OIL;
    public static Material NEUTRALIZED_COOKING_OIL;

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
    }
}
