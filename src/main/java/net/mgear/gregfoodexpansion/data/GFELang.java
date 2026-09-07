package net.mgear.gregfoodexpansion.data;

import net.mgear.gregfoodexpansion.registry.GFERegistration;

/**
 * en_us 语言条目通过 {@link GFERegistration#REGISTRATE} 的 addRawLang 登记并由 runData 生成;
 * zh_cn 手工维护于 src/main/resources/assets/gregfoodexpansion/lang/zh_cn.json。
 */
public final class GFELang {
    private GFELang() {}

    public static void init() {
        // 创造模式标签标题由 GFECreativeModeTabs 的 addLang 生成。

        add("block.gregfoodexpansion.soybean_crop", "Soybean Crop");
        add("block.gregfoodexpansion.corn_crop", "Corn Crop");
        add("block.gregfoodexpansion.rice_crop", "Rice Crop");
        add("block.gregfoodexpansion.barley_crop", "Barley Crop");
        add("block.gregfoodexpansion.peanut_crop", "Peanut Crop");
        add("block.gregfoodexpansion.tomato_crop", "Tomato Crop");
        add("block.gregfoodexpansion.onion_crop", "Onion Crop");
        add("block.gregfoodexpansion.chili_crop", "Chili Crop");
        add("block.gregfoodexpansion.cabbage_crop", "Cabbage Crop");
        add("block.gregfoodexpansion.grape_crop", "Grape Crop");
        add("block.gregfoodexpansion.coffee_crop", "Coffee Crop");
        add("block.gregfoodexpansion.tea_crop", "Tea Crop");
        add("block.gregfoodexpansion.hops_crop", "Hops Crop");

        add("item.gregfoodexpansion.soybean_seeds", "Soybean Seeds");
        add("item.gregfoodexpansion.corn_seeds", "Corn Seeds");
        add("item.gregfoodexpansion.rice_seeds", "Rice Seeds");
        add("item.gregfoodexpansion.barley_seeds", "Barley Seeds");
        add("item.gregfoodexpansion.peanut_seeds", "Peanut Seeds");
        add("item.gregfoodexpansion.tomato_seeds", "Tomato Seeds");
        add("item.gregfoodexpansion.onion_seeds", "Onion Seeds");
        add("item.gregfoodexpansion.chili_seeds", "Chili Pepper Seeds");
        add("item.gregfoodexpansion.cabbage_seeds", "Cabbage Seeds");
        add("item.gregfoodexpansion.grape_seeds", "Grape Seeds");
        add("item.gregfoodexpansion.coffee_seeds", "Coffee Seeds");
        add("item.gregfoodexpansion.tea_seeds", "Tea Seeds");
        add("item.gregfoodexpansion.hops_seeds", "Hops Seeds");

        add("item.gregfoodexpansion.soybean", "Soybean");
        add("item.gregfoodexpansion.corn", "Corn");
        add("item.gregfoodexpansion.rice", "Rice");
        add("item.gregfoodexpansion.barley", "Barley");
        add("item.gregfoodexpansion.peanut", "Peanut");
        add("item.gregfoodexpansion.tomato", "Tomato");
        add("item.gregfoodexpansion.onion", "Onion");
        add("item.gregfoodexpansion.chili", "Chili Pepper");
        add("item.gregfoodexpansion.cabbage", "Cabbage");
        add("item.gregfoodexpansion.grape", "Grapes");
        add("item.gregfoodexpansion.coffee_cherries", "Coffee Cherries");
        add("item.gregfoodexpansion.tea", "Tea Leaves");
        add("item.gregfoodexpansion.hops", "Hops");

        add("block.gregfoodexpansion.wild_soybean", "Wild Soybean");
        add("block.gregfoodexpansion.wild_corn", "Wild Corn");
        add("block.gregfoodexpansion.wild_rice", "Wild Rice");
        add("block.gregfoodexpansion.wild_barley", "Wild Barley");
        add("block.gregfoodexpansion.wild_peanut", "Wild Peanut");
        add("block.gregfoodexpansion.wild_tomato", "Wild Tomato");
        add("block.gregfoodexpansion.wild_onion", "Wild Onion");
        add("block.gregfoodexpansion.wild_chili", "Wild Chili Pepper");
        add("block.gregfoodexpansion.wild_cabbage", "Wild Cabbage");
        add("block.gregfoodexpansion.wild_grape", "Wild Grapevine");
        add("block.gregfoodexpansion.wild_coffee", "Wild Coffee Shrub");
        add("block.gregfoodexpansion.wild_tea", "Wild Tea Plant");
        add("block.gregfoodexpansion.wild_hops", "Wild Hops");
    }

    private static void add(String key, String value) {
        GFERegistration.REGISTRATE.addRawLang(key, value);
    }
}
