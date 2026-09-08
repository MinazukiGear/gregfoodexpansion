package net.mgear.gregfoodexpansion.data;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.registry.GTFEMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

/**
 * 流体标签(soybean-chain.md §2,forge 命名空间语义层):
 * - 豆油 → #forge:cooking_oil(M1 食用油源,炒/炸模式通用引用);
 * - 生抽/老抽 → #forge:soy_sauces(通用调味引用,变体间不可互换的硬引用走本体)。
 * Registrate 1.3.11 无独立流体标签 provider,FLUID_TAGS 为
 * RegistrateTagsProvider.IntrinsicImpl<Fluid>(ProviderType.java 反编译确认)。
 */
public final class GFEFluidTags {
    private GFEFluidTags() {}

    public static void init(RegistrateTagsProvider.IntrinsicImpl<Fluid> provider) {
        provider.addTag(forgeTag("cooking_oil"))
                .add(GTMaterials.SeedOil.getFluid())
                .add(GTFEMaterials.SOYBEAN_OIL.getFluid());
        provider.addTag(forgeTag("soy_sauces"))
                .add(GTFEMaterials.SOY_SAUCE.getFluid())
                .add(GTFEMaterials.DARK_SOY_SAUCE.getFluid());
    }

    private static TagKey<Fluid> forgeTag(String path) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("forge", path));
    }
}
