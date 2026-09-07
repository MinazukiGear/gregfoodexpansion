package net.mgear.gregfoodexpansion.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 首批 13 作物的种子与产物物品(crop-system-foundation.md §2)。
 * 种子 = ItemNameBlockItem(种植对应作物方块);作物方块本身无 BlockItem;
 * 咖啡产物为 coffee_cherries(咖啡浆果,烘焙得咖啡豆属加工链)。
 */
public final class GFECropItems {
    private GFECropItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GregFoodExpansion.MOD_ID);

    public static final RegistryObject<Item> SOYBEAN_SEEDS = seed("soybean", GFECropBlocks.SOYBEAN_CROP);
    public static final RegistryObject<Item> CORN_SEEDS = seed("corn", GFECropBlocks.CORN_CROP);
    public static final RegistryObject<Item> RICE_SEEDS = seed("rice", GFECropBlocks.RICE_CROP);
    public static final RegistryObject<Item> BARLEY_SEEDS = seed("barley", GFECropBlocks.BARLEY_CROP);
    public static final RegistryObject<Item> PEANUT_SEEDS = seed("peanut", GFECropBlocks.PEANUT_CROP);
    public static final RegistryObject<Item> TOMATO_SEEDS = seed("tomato", GFECropBlocks.TOMATO_CROP);
    public static final RegistryObject<Item> ONION_SEEDS = seed("onion", GFECropBlocks.ONION_CROP);
    public static final RegistryObject<Item> CHILI_SEEDS = seed("chili", GFECropBlocks.CHILI_CROP);
    public static final RegistryObject<Item> CABBAGE_SEEDS = seed("cabbage", GFECropBlocks.CABBAGE_CROP);
    public static final RegistryObject<Item> GRAPE_SEEDS = seed("grape", GFECropBlocks.GRAPE_CROP);
    public static final RegistryObject<Item> COFFEE_SEEDS = seed("coffee", GFECropBlocks.COFFEE_CROP);
    public static final RegistryObject<Item> TEA_SEEDS = seed("tea", GFECropBlocks.TEA_CROP);
    public static final RegistryObject<Item> HOPS_SEEDS = seed("hops", GFECropBlocks.HOPS_CROP);

    public static final RegistryObject<Item> SOYBEAN = product("soybean");
    public static final RegistryObject<Item> CORN = product("corn");
    public static final RegistryObject<Item> RICE = product("rice");
    public static final RegistryObject<Item> BARLEY = product("barley");
    public static final RegistryObject<Item> PEANUT = product("peanut");
    public static final RegistryObject<Item> TOMATO = product("tomato");
    public static final RegistryObject<Item> ONION = product("onion");
    public static final RegistryObject<Item> CHILI = product("chili");
    public static final RegistryObject<Item> CABBAGE = product("cabbage");
    public static final RegistryObject<Item> GRAPE = product("grape");
    public static final RegistryObject<Item> COFFEE_CHERRIES = product("coffee_cherries");
    public static final RegistryObject<Item> TEA = product("tea");
    public static final RegistryObject<Item> HOPS = product("hops");

    /** 全部种子(注册序 = 创造标签展示序,crop-system-foundation.md §2:种子 → 作物 → 食品)。 */
    public static final List<RegistryObject<Item>> ALL_SEEDS = List.of(
            SOYBEAN_SEEDS, CORN_SEEDS, RICE_SEEDS, BARLEY_SEEDS, PEANUT_SEEDS, TOMATO_SEEDS,
            ONION_SEEDS, CHILI_SEEDS, CABBAGE_SEEDS, GRAPE_SEEDS, COFFEE_SEEDS, TEA_SEEDS, HOPS_SEEDS);

    public static final List<RegistryObject<Item>> ALL_PRODUCTS = List.of(
            SOYBEAN, CORN, RICE, BARLEY, PEANUT, TOMATO, ONION, CHILI, CABBAGE, GRAPE,
            COFFEE_CHERRIES, TEA, HOPS);

    private static final Map<String, RegistryObject<Item>> SEEDS_BY_CROP = new LinkedHashMap<>();

    static {
        ALL_SEEDS.forEach(item -> {
            String crop = item.getId().getPath().replace("_seeds", "");
            SEEDS_BY_CROP.put(crop, item);
        });
    }

    public static RegistryObject<Item> seedOf(String crop) {
        return SEEDS_BY_CROP.get(crop);
    }

    private static RegistryObject<Item> seed(String crop, RegistryObject<Block> cropBlock) {
        return ITEMS.register(crop + "_seeds",
                () -> new ItemNameBlockItem(cropBlock.get(), new Item.Properties()));
    }

    private static RegistryObject<Item> product(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }
}
