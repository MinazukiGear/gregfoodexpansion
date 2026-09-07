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

        // ---- 食材形态(dishes-and-gains.md §4) ----
        add("item.gregfoodexpansion.beef_slice", "Beef Slice");
        add("item.gregfoodexpansion.beef_strip", "Beef Strips");
        add("item.gregfoodexpansion.beef_cube", "Beef Cubes");
        add("item.gregfoodexpansion.beef_ribs", "Beef Ribs");
        add("item.gregfoodexpansion.beef_minced", "Minced Beef");
        add("item.gregfoodexpansion.pork_slice", "Pork Slice");
        add("item.gregfoodexpansion.pork_strip", "Pork Strips");
        add("item.gregfoodexpansion.pork_cube", "Pork Cubes");
        add("item.gregfoodexpansion.pork_ribs", "Pork Ribs");
        add("item.gregfoodexpansion.pork_minced", "Minced Pork");
        add("item.gregfoodexpansion.mutton_slice", "Mutton Slice");
        add("item.gregfoodexpansion.mutton_cube", "Mutton Cubes");
        add("item.gregfoodexpansion.mutton_minced", "Minced Mutton");
        add("item.gregfoodexpansion.chicken_slice", "Chicken Slice");
        add("item.gregfoodexpansion.chicken_shred", "Shredded Chicken");
        add("item.gregfoodexpansion.chicken_diced", "Diced Chicken");
        add("item.gregfoodexpansion.chicken_cuts", "Chicken Cuts");
        add("item.gregfoodexpansion.chicken_minced", "Minced Chicken");
        add("item.gregfoodexpansion.fish_slice", "Fish Fillet");
        add("item.gregfoodexpansion.fish_cube", "Fish Cubes");
        add("item.gregfoodexpansion.fish_surimi", "Fish Surimi");
        add("item.gregfoodexpansion.chili_ring", "Chili Rings");
        add("item.gregfoodexpansion.potato_slice", "Potato Slices");
        add("item.gregfoodexpansion.apple_slice", "Apple Slices");
        add("item.gregfoodexpansion.tomato_slice", "Tomato Slices");
        add("item.gregfoodexpansion.potato_strip", "Potato Strips");
        add("item.gregfoodexpansion.carrot_strip", "Carrot Strips");
        add("item.gregfoodexpansion.cabbage_strip", "Cabbage Strips");
        add("item.gregfoodexpansion.chili_strip", "Chili Strips");
        add("item.gregfoodexpansion.tomato_diced", "Diced Tomato");
        add("item.gregfoodexpansion.onion_diced", "Diced Onion");
        add("item.gregfoodexpansion.fries_blank", "Raw Fries");
        add("item.gregfoodexpansion.chili_diced", "Diced Chili");
        add("item.gregfoodexpansion.onion_minced", "Minced Onion");
        add("item.gregfoodexpansion.chili_powder", "Chili Powder");
        add("item.gregfoodexpansion.apple_flesh", "Peeled Apple");
        add("item.gregfoodexpansion.noodle", "Noodles");
        add("item.gregfoodexpansion.dough_sheet", "Dough Sheet");

        // ---- 手工切配工具(food-processor.md §7) ----
        add("item.gregfoodexpansion.cleaver", "Cleaver");
        add("item.gregfoodexpansion.peeler", "Peeler");
        add("item.gregfoodexpansion.mortar_pestle", "Mortar and Pestle");
        add("item.gregfoodexpansion.rolling_pin", "Rolling Pin");
    }

    private static void add(String key, String value) {
        GFERegistration.REGISTRATE.addRawLang(key, value);
    }
}
