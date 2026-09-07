package net.mgear.gregfoodexpansion.data;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.prep.GFEFormItems;
import net.mgear.gregfoodexpansion.registry.GFECropBlocks;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

/**
 * 三层标签策略落地(compatibility-boundary.md §3):每作物注册 forge:crops/<crop>(产物)、
 * forge:seeds/<crop>(种子);谷物类另注册 forge:grain/<crop>(barley、rice,沿用
 * GTCEu CustomTags 既有口径)。①/② 层标签一律为 forge 命名空间(跨模组互通的语义层),
 * gregfoodexpansion:* 私有分组是 ③ 层,不在此处使用。本模组配方只引用标签,不硬编码物品 id。
 */
public final class GFEItemTags {
    private GFEItemTags() {}

    public static void init(RegistrateItemTagsProvider provider) {
        for (int i = 0; i < GFECropBlocks.ALL_CROPS.size(); i++) {
            String crop = cropName(GFECropBlocks.ALL_CROPS.get(i));
            provider.addTag(forgeTag("crops/" + crop)).add(GFECropItems.ALL_PRODUCTS.get(i).get());
            provider.addTag(forgeTag("seeds/" + crop)).add(GFECropItems.ALL_SEEDS.get(i).get());
        }
        // 谷物类(crop-system-foundation.md §7):barley、rice 进 forge:grain。
        provider.addTag(forgeTag("grain/barley")).add(GFECropItems.BARLEY.get());
        provider.addTag(forgeTag("grain/rice")).add(GFECropItems.RICE.get());

        // 形态标签(compatibility-boundary.md §3 第②层,dishes-and-gains.md §4):
        // 肉形态按种类单品注册,通用配方引用走形态标签。
        GFEFormItems.MEAT_SLICES.forEach(item -> provider.addTag(forgeTag("sliced_meat")).add(item.get()));
        GFEFormItems.MEAT_STRIPS.forEach(item -> provider.addTag(forgeTag("meat_strips")).add(item.get()));
        GFEFormItems.MEAT_CUBES.forEach(item -> provider.addTag(forgeTag("meat_cubes")).add(item.get()));
        GFEFormItems.MINCED_MEATS.forEach(item -> provider.addTag(forgeTag("minced_meat")).add(item.get()));
    }

    // 方块注册名是 <crop>_crop,取作物名需去掉后缀。
    private static String cropName(RegistryObject<?> cropBlock) {
        String path = cropBlock.getId().getPath();
        return path.endsWith("_crop") ? path.substring(0, path.length() - "_crop".length()) : path;
    }

    private static TagKey<Item> forgeTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", path));
    }
}
