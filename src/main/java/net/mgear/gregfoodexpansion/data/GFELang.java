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
        add("item.gregfoodexpansion.potato_diced", "Diced Potato");
        add("item.gregfoodexpansion.onion_minced", "Minced Onion");
        add("item.gregfoodexpansion.garlic_minced", "Minced Garlic");
        add("item.gregfoodexpansion.chili_powder", "Chili Powder");
        add("item.gregfoodexpansion.apple_flesh", "Peeled Apple");
        add("item.gregfoodexpansion.noodle", "Noodles");
        add("item.gregfoodexpansion.dough_sheet", "Dough Sheet");
        add("item.gregfoodexpansion.rice_flour", "Rice Flour");
        add("item.gregfoodexpansion.rice_dough", "Rice Dough");
        add("item.gregfoodexpansion.rice_noodles", "Rice Noodles");

        // ---- 精制档菜肴(dishes-and-gains.md §5) ----
        add("item.gregfoodexpansion.rice_noodle_soup", "Rice Noodle Soup");
        add("item.gregfoodexpansion.tomato_soup", "Tomato Soup");
        add("item.gregfoodexpansion.vegetable_soup", "Vegetable Soup");
        add("item.gregfoodexpansion.rib_soup", "Rib Soup");
        add("item.gregfoodexpansion.rice_porridge", "Rice Porridge");
        add("item.gregfoodexpansion.corn_soup", "Corn Soup");
        add("item.gregfoodexpansion.dumplings", "Dumplings");
        add("item.gregfoodexpansion.steamed_rice", "Steamed Rice");
        add("item.gregfoodexpansion.mantou", "Mantou");
        add("item.gregfoodexpansion.baozi", "Baozi");
        add("item.gregfoodexpansion.steamed_egg", "Steamed Egg");
        add("item.gregfoodexpansion.steamed_corn", "Steamed Corn");
        add("item.gregfoodexpansion.rice_cake", "Rice Cake");
        add("item.gregfoodexpansion.rice_steamed_pork", "Rice-Flour Steamed Pork");
        add("item.gregfoodexpansion.white_cake", "White Cake");
        add("item.gregfoodexpansion.tomato_scrambled_egg", "Scrambled Eggs with Tomato");
        add("item.gregfoodexpansion.fried_rice", "Fried Rice");
        add("item.gregfoodexpansion.fried_noodles", "Fried Noodles");
        add("item.gregfoodexpansion.chili_shredded_pork", "Shredded Pork with Chili");
        add("item.gregfoodexpansion.stir_fried_pork", "Stir-Fried Pork with Chili");
        add("item.gregfoodexpansion.kung_pao_chicken", "Kung Pao Chicken");
        add("item.gregfoodexpansion.stir_fried_vegetables", "Stir-Fried Vegetables");
        add("item.gregfoodexpansion.fried_rice_noodles", "Stir-Fried Rice Noodles");
        add("item.gregfoodexpansion.beef_chow_fun", "Beef Chow Fun");
        add("item.gregfoodexpansion.fries", "Fries");
        add("item.gregfoodexpansion.potato_chips", "Potato Chips");
        add("item.gregfoodexpansion.fried_chicken_cuts", "Fried Chicken Cuts");
        add("item.gregfoodexpansion.fried_peanuts", "Fried Peanuts");
        add("item.gregfoodexpansion.fried_fish_fillet", "Fried Fish Fillet");
        add("item.gregfoodexpansion.onion_rings", "Onion Rings");
        add("item.gregfoodexpansion.spring_roll", "Spring Rolls");
        add("item.gregfoodexpansion.rice_cracker", "Rice Crackers");

        // ---- 烘焙(tunnel-oven.md) ----
        add("block.gregfoodexpansion.tunnel_oven_casing", "Tunnel Oven Casing");
        add("block.gregfoodexpansion.tunnel_oven_belt", "Tunnel Oven Belt");
        add("block.gregfoodexpansion.tunnel_oven_heater", "Tunnel Oven Heater");
        add("block.gregfoodexpansion.tunnel_oven_vent", "Tunnel Oven Vent");
        add("item.gregfoodexpansion.bread", "Bread");
        add("item.gregfoodexpansion.toast", "Toast");
        add("item.gregfoodexpansion.sweet_bread", "Sweet Bread");
        add("item.gregfoodexpansion.cake", "Cake");
        add("item.gregfoodexpansion.apple_pie", "Apple Pie");
        add("item.gregfoodexpansion.baguette", "Baguette");
        add("item.gregfoodexpansion.dinner_roll", "Dinner Roll");
        add("item.gregfoodexpansion.corn_bread", "Corn Bread");
        add("item.gregfoodexpansion.baked_corn", "Baked Corn");
        add("item.gregfoodexpansion.garlic_baguette", "Garlic Baguette");
        add("item.gregfoodexpansion.bread_slice", "Bread Slice");
        add("item.gregfoodexpansion.baguette_slice", "Baguette Slice");
        add("item.gregfoodexpansion.burger_bun", "Burger Bun");
        add("item.gregfoodexpansion.raw_bread", "Raw Bread Loaf");
        add("item.gregfoodexpansion.raw_baguette", "Raw Baguette");
        add("item.gregfoodexpansion.raw_toast", "Raw Toast Loaf");
        add("item.gregfoodexpansion.sliced_bread", "Sliced Bread");
        add("item.gregfoodexpansion.sliced_baguetted", "Sliced Baguette");
        add("item.gregfoodexpansion.sliced_burger_bun", "Sliced Burger Bun");

        // ---- 基础档手工菜肴(dishes-and-gains.md §5,LV 前兜底) ----
        add("item.gregfoodexpansion.fruit_platter", "Fruit Platter");
        add("item.gregfoodexpansion.sugar_tomato", "Tomatoes with Sugar");
        add("item.gregfoodexpansion.chicken_cold_noodles", "Cold Chicken Noodles");
        add("item.gregfoodexpansion.fried_egg", "Fried Egg");
        add("item.gregfoodexpansion.plain_noodles", "Plain Noodles");
        add("item.gregfoodexpansion.hand_fried_rice", "Handmade Egg Fried Rice");
        add("item.gregfoodexpansion.hand_steamed_egg", "Handmade Steamed Egg");
        add("item.gregfoodexpansion.hand_steamed_corn", "Handmade Steamed Corn");

        add("item.gregfoodexpansion.kitchen_knife", "Kitchen Knife");
        add("item.gregfoodexpansion.wok", "Wok");
        add("item.gregfoodexpansion.steamer", "Steamer");

        // 注:自有材料(GTFEMaterials)的 en 名由 GTRegistrate 自动写入 lang;
        // zh_cn 手工维护于 src/main/resources/assets/gregfoodexpansion/lang/zh_cn.json。

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
