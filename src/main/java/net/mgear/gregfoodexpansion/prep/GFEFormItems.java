package net.mgear.gregfoodexpansion.prep;

import java.util.List;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 食材形态物品(dishes-and-gains.md §4 首批名录):
 * 肉类形态按种类区分、以原版生肉为源;非肉形态来自首批作物与原版蔬果。
 * 2026-09-08 决议:蒜末直接注册(配方待大蒜原料落地,P2);土豆丁由薯条坯二次切配。
 * 未注册项:米粉(米粉团 M1 无原料)、烘焙切分 3 项(随烘焙工业化批次)。
 * 食材中间品不带品质档(food-processor.md §6)。
 */
public final class GFEFormItems {
    private GFEFormItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GregFoodExpansion.MOD_ID);

    // ---- 肉类:牛肉 ----
    public static final RegistryObject<Item> BEEF_SLICE = form("beef_slice");
    public static final RegistryObject<Item> BEEF_STRIP = form("beef_strip");
    public static final RegistryObject<Item> BEEF_CUBE = form("beef_cube");
    public static final RegistryObject<Item> BEEF_RIBS = form("beef_ribs");
    public static final RegistryObject<Item> BEEF_MINCED = form("beef_minced");
    // ---- 肉类:猪肉 ----
    public static final RegistryObject<Item> PORK_SLICE = form("pork_slice");
    public static final RegistryObject<Item> PORK_STRIP = form("pork_strip");
    public static final RegistryObject<Item> PORK_CUBE = form("pork_cube");
    public static final RegistryObject<Item> PORK_RIBS = form("pork_ribs");
    public static final RegistryObject<Item> PORK_MINCED = form("pork_minced");
    // ---- 肉类:羊肉 ----
    public static final RegistryObject<Item> MUTTON_SLICE = form("mutton_slice");
    public static final RegistryObject<Item> MUTTON_CUBE = form("mutton_cube");
    public static final RegistryObject<Item> MUTTON_MINCED = form("mutton_minced");
    // ---- 肉类:鸡肉(肉丝仅熟鸡,可直接食用) ----
    public static final RegistryObject<Item> CHICKEN_SLICE = form("chicken_slice");
    public static final RegistryObject<Item> CHICKEN_SHRED = cooked("chicken_shred");
    public static final RegistryObject<Item> CHICKEN_DICED = form("chicken_diced");
    public static final RegistryObject<Item> CHICKEN_CUTS = form("chicken_cuts");
    public static final RegistryObject<Item> CHICKEN_MINCED = form("chicken_minced");
    // 生鸡排(鸡肉片压延拍平)/生鸡柳条(鸡肉片切条):挂糊炸制的前置(2026-09-08)
    public static final RegistryObject<Item> RAW_CHICKEN_CUTLET = form("raw_chicken_cutlet");
    public static final RegistryObject<Item> RAW_CHICKEN_TENDER = form("raw_chicken_tender");
    // ---- 肉类:鱼 ----
    public static final RegistryObject<Item> FISH_SLICE = form("fish_slice");
    public static final RegistryObject<Item> FISH_CUBE = form("fish_cube");
    public static final RegistryObject<Item> FISH_SURIMI = form("fish_surimi");
    // ---- 非肉:切片 ----
    public static final RegistryObject<Item> CHILI_RING = form("chili_ring");
    public static final RegistryObject<Item> POTATO_SLICE = form("potato_slice");
    public static final RegistryObject<Item> APPLE_SLICE = form("apple_slice");
    public static final RegistryObject<Item> TOMATO_SLICE = form("tomato_slice");
    // ---- 非肉:切丝 ----
    public static final RegistryObject<Item> POTATO_STRIP = form("potato_strip");
    public static final RegistryObject<Item> CARROT_STRIP = form("carrot_strip");
    public static final RegistryObject<Item> CABBAGE_STRIP = form("cabbage_strip");
    public static final RegistryObject<Item> CHILI_STRIP = form("chili_strip");
    // ---- 非肉:切块 ----
    public static final RegistryObject<Item> TOMATO_DICED = form("tomato_diced");
    public static final RegistryObject<Item> ONION_DICED = form("onion_diced");
    public static final RegistryObject<Item> FRIES_BLANK = form("fries_blank");
    public static final RegistryObject<Item> CHILI_DICED = form("chili_diced");
    public static final RegistryObject<Item> POTATO_DICED = form("potato_diced");
    // ---- 非肉:绞碎/研磨/剥皮 ----
    public static final RegistryObject<Item> ONION_MINCED = form("onion_minced");
    public static final RegistryObject<Item> GARLIC_MINCED = form("garlic_minced");
    public static final RegistryObject<Item> CHILI_POWDER = form("chili_powder");
    public static final RegistryObject<Item> APPLE_FLESH = form("apple_flesh");
    // ---- 非肉:压延 ----
    public static final RegistryObject<Item> NOODLE = form("noodle");
    public static final RegistryObject<Item> DOUGH_SHEET = form("dough_sheet");
    // ---- 烘焙生坯(2026-09-08:面团与烘焙品之间的成型转换,压延/擀面杖承载) ----
    public static final RegistryObject<Item> RAW_BREAD = form("raw_bread");
    public static final RegistryObject<Item> RAW_TOAST = form("raw_toast");
    public static final RegistryObject<Item> RAW_BAGUETTE = form("raw_baguette");
    public static final RegistryObject<Item> RAW_DINNER_ROLL = form("raw_dinner_roll");
    // ---- 肉饼链(dishes-and-gains.md §5 装配与肉饼链):馅 → 冲压 → 生肉饼 → 煎/烤 → 熟肉饼 ----
    public static final RegistryObject<Item> RAW_BEEF_PATTY = form("raw_beef_patty");
    public static final RegistryObject<Item> COOKED_BEEF_PATTY = cookedPatty("cooked_beef_patty", 6, 0.6F);
    // ---- 烘焙切分(dishes-and-gains.md §4):切片电路承载,三明治/汉堡与蒜香法棍的原料 ----
    public static final RegistryObject<Item> BREAD_SLICE = form("bread_slice");
    public static final RegistryObject<Item> BAGUETTE_SLICE = form("baguette_slice");
    public static final RegistryObject<Item> BURGER_BUN = form("burger_bun");
    // ---- 切好件(2026-09-08:切片 ×2 工作台拼装为一份即用切片,装配基座与库存密度) ----
    public static final RegistryObject<Item> SLICED_BREAD = form("sliced_bread");
    public static final RegistryObject<Item> SLICED_BAGUETTE = form("sliced_baguetted");
    public static final RegistryObject<Item> SLICED_BURGER_BUN = form("sliced_burger_bun");
    // ---- 米制品链(2026-09-08 提案落地):水稻 → 研磨 → 大米粉 → +水 → 米粉团 → 压延 → 米粉条 ----
    public static final RegistryObject<Item> RICE_FLOUR = form("rice_flour");
    public static final RegistryObject<Item> RICE_DOUGH = form("rice_dough");
    public static final RegistryObject<Item> RICE_NOODLES = form("rice_noodles");

    public static final List<RegistryObject<Item>> ALL = List.of(
            BEEF_SLICE, BEEF_STRIP, BEEF_CUBE, BEEF_RIBS, BEEF_MINCED,
            PORK_SLICE, PORK_STRIP, PORK_CUBE, PORK_RIBS, PORK_MINCED,
            MUTTON_SLICE, MUTTON_CUBE, MUTTON_MINCED,
            CHICKEN_SLICE, CHICKEN_SHRED, CHICKEN_DICED, CHICKEN_CUTS, CHICKEN_MINCED,
            RAW_CHICKEN_CUTLET, RAW_CHICKEN_TENDER,
            RAW_BEEF_PATTY, COOKED_BEEF_PATTY,
            FISH_SLICE, FISH_CUBE, FISH_SURIMI,
            CHILI_RING, POTATO_SLICE, APPLE_SLICE, TOMATO_SLICE,
            POTATO_STRIP, CARROT_STRIP, CABBAGE_STRIP, CHILI_STRIP,
            TOMATO_DICED, ONION_DICED, FRIES_BLANK, CHILI_DICED, POTATO_DICED,
            ONION_MINCED, GARLIC_MINCED, CHILI_POWDER, APPLE_FLESH, NOODLE, DOUGH_SHEET,
            BREAD_SLICE, BAGUETTE_SLICE, BURGER_BUN,
            SLICED_BREAD, SLICED_BAGUETTE, SLICED_BURGER_BUN,
            RAW_BREAD, RAW_TOAST, RAW_BAGUETTE, RAW_DINNER_ROLL,
            RICE_FLOUR, RICE_DOUGH, RICE_NOODLES);

    public static final List<RegistryObject<Item>> MEAT_SLICES = List.of(
            BEEF_SLICE, PORK_SLICE, MUTTON_SLICE, CHICKEN_SLICE, FISH_SLICE);
    public static final List<RegistryObject<Item>> MEAT_STRIPS = List.of(
            BEEF_STRIP, PORK_STRIP, CHICKEN_SHRED);
    public static final List<RegistryObject<Item>> MEAT_CUBES = List.of(
            BEEF_CUBE, BEEF_RIBS, PORK_CUBE, PORK_RIBS, MUTTON_CUBE,
            CHICKEN_DICED, CHICKEN_CUTS, FISH_CUBE);
    public static final List<RegistryObject<Item>> MINCED_MEATS = List.of(
            BEEF_MINCED, PORK_MINCED, MUTTON_MINCED, CHICKEN_MINCED, FISH_SURIMI);

    private static RegistryObject<Item> form(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    // 熟制形态继承熟制状态,可直接食用(dishes-and-gains.md §4)。
    private static RegistryObject<Item> cooked(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(
                new net.minecraft.world.food.FoodProperties.Builder()
                        .nutrition(1)
                        .saturationMod(0.1F)
                        .build())));
    }

    // 熟肉饼为烹饪机精制档产出(急迫 I 5 min)
    private static RegistryObject<Item> cookedPatty(String name, int hunger, float saturation) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(
                new net.minecraft.world.food.FoodProperties.Builder()
                        .nutrition(hunger)
                        .saturationMod(saturation)
                        .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.world.effect.MobEffects.DIG_SPEED, 6000, 0), 1.0F)
                        .build())));
    }

    /** 配方书写便利重载。 */
    public static ItemLike item(RegistryObject<Item> item) {
        return item.get();
    }
}
