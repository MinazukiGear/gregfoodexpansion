package net.mgear.gregfoodexpansion.cooking;

import java.util.List;
import java.util.function.Supplier;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 精制档菜肴(通用烹饪机产出,dishes-and-gains.md §2/§5)。
 * 饱足增益按烹饪模式定案(c1 煮=急迫+再生 / c2 蒸=急迫+吸收 / c3 炒=急迫 II /
 * c4 炸=急迫+速度+高饱和);饥饿 2-12、饱和 0.3-1.0 区间,数值为草案基准(universal-cooker.md §6)。
 * 长度单位 = tick(20/s):5 min = 6000,4 min = 4800,3 min = 3600,30 s = 600。
 * SoL/味精等修正项在增益结算管线(§2.1)实现,首版为基准时长。
 */
public final class GFEDishes {
    private GFEDishes() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GregFoodExpansion.MOD_ID);

    // ---- c1 煮 ----
    public static final RegistryObject<Item> RICE_NOODLE_SOUP = soup("rice_noodle_soup", 6, 0.4F);
    public static final RegistryObject<Item> TOMATO_SOUP = soup("tomato_soup", 4, 0.4F);
    public static final RegistryObject<Item> VEGETABLE_SOUP = soup("vegetable_soup", 4, 0.4F);
    public static final RegistryObject<Item> RIB_SOUP = soup("rib_soup", 8, 0.5F);
    public static final RegistryObject<Item> RICE_PORRIDGE = soup("rice_porridge", 3, 0.3F);
    public static final RegistryObject<Item> CORN_SOUP = soup("corn_soup", 5, 0.4F);
    public static final RegistryObject<Item> DUMPLINGS = soup("dumplings", 8, 0.5F);

    // ---- c2 蒸 ----
    public static final RegistryObject<Item> STEAMED_RICE = steamed("steamed_rice", 4, 0.4F);
    public static final RegistryObject<Item> MANTOU = steamed("mantou", 4, 0.4F);
    public static final RegistryObject<Item> BAOZI = steamed("baozi", 8, 0.5F);
    public static final RegistryObject<Item> STEAMED_EGG = steamed("steamed_egg", 4, 0.4F);
    public static final RegistryObject<Item> STEAMED_CORN = steamed("steamed_corn", 2, 0.3F);
    public static final RegistryObject<Item> RICE_CAKE = steamed("rice_cake", 5, 0.4F);
    public static final RegistryObject<Item> RICE_STEAMED_PORK = steamed("rice_steamed_pork", 10, 0.6F);
    public static final RegistryObject<Item> WHITE_CAKE = steamed("white_cake", 5, 0.4F);

    // ---- c3 炒(急迫 II,大菜) ----
    public static final RegistryObject<Item> TOMATO_SCRAMBLED_EGG = stirfry("tomato_scrambled_egg", 6, 0.6F);
    public static final RegistryObject<Item> FRIED_RICE = stirfry("fried_rice", 8, 0.6F);
    public static final RegistryObject<Item> FRIED_NOODLES = stirfry("fried_noodles", 8, 0.6F);
    public static final RegistryObject<Item> CHILI_SHREDDED_PORK = stirfry("chili_shredded_pork", 8, 0.6F);
    public static final RegistryObject<Item> STIR_FRIED_PORK = stirfry("stir_fried_pork", 9, 0.7F);
    public static final RegistryObject<Item> KUNG_PAO_CHICKEN = stirfry("kung_pao_chicken", 10, 0.7F);
    public static final RegistryObject<Item> STIR_FRIED_VEGETABLES = stirfry("stir_fried_vegetables", 5, 0.5F);
    public static final RegistryObject<Item> FRIED_RICE_NOODLES = stirfry("fried_rice_noodles", 8, 0.6F);
    public static final RegistryObject<Item> BEEF_CHOW_FUN = stirfry("beef_chow_fun", 10, 0.7F);

    // ---- c4 炸(高饱和) ----
    public static final RegistryObject<Item> FRIES = fried("fries", 5, 0.8F);
    public static final RegistryObject<Item> POTATO_CHIPS = fried("potato_chips", 3, 0.8F);
    public static final RegistryObject<Item> FRIED_CHICKEN_CUTS = fried("fried_chicken_cuts", 6, 0.8F);
    public static final RegistryObject<Item> FRIED_PEANUTS = fried("fried_peanuts", 3, 0.8F);
    public static final RegistryObject<Item> FRIED_FISH_FILLET = fried("fried_fish_fillet", 6, 0.8F);
    public static final RegistryObject<Item> ONION_RINGS = fried("onion_rings", 4, 0.8F);
    public static final RegistryObject<Item> SPRING_ROLL = fried("spring_roll", 6, 0.8F);
    public static final RegistryObject<Item> RICE_CRACKER = fried("rice_cracker", 3, 0.8F);
    public static final RegistryObject<Item> CHICKEN_CUTLET = fried("chicken_cutlet", 6, 0.7F);
    public static final RegistryObject<Item> CHICKEN_TENDER = fried("chicken_tender", 5, 0.7F);

    // ---- 基础档(手工,dishes-and-gains.md §2/§5):急迫 I 15 s 微效果,无主增益 ----
    public static final RegistryObject<Item> FRUIT_PLATTER = hand("fruit_platter", 4, 0.3F);
    public static final RegistryObject<Item> SUGAR_TOMATO = hand("sugar_tomato", 3, 0.3F);
    public static final RegistryObject<Item> CHICKEN_COLD_NOODLES = hand("chicken_cold_noodles", 6, 0.4F);
    public static final RegistryObject<Item> FRIED_EGG = hand("fried_egg", 3, 0.4F);
    public static final RegistryObject<Item> PLAIN_NOODLES = hand("plain_noodles", 5, 0.4F);
    public static final RegistryObject<Item> HAND_FRIED_RICE = hand("hand_fried_rice", 5, 0.4F);
    public static final RegistryObject<Item> HAND_STEAMED_EGG = hand("hand_steamed_egg", 3, 0.3F);
    public static final RegistryObject<Item> HAND_STEAMED_CORN = hand("hand_steamed_corn", 2, 0.3F);

    // ---- c5 烤(隧道式烤炉/通用烹饪机电路 5):急迫 I 5 min + 饱和(瞬时),烘焙主食向 ----
    public static final RegistryObject<Item> BREAD = baked("bread", 5, 0.6F);
    public static final RegistryObject<Item> TOAST = baked("toast", 4, 0.6F);
    public static final RegistryObject<Item> SWEET_BREAD = baked("sweet_bread", 5, 0.6F);
    public static final RegistryObject<Item> CAKE = baked("cake", 6, 0.5F);
    public static final RegistryObject<Item> APPLE_PIE = baked("apple_pie", 6, 0.6F);
    public static final RegistryObject<Item> BAGUETTE = baked("baguette", 5, 0.6F);
    public static final RegistryObject<Item> DINNER_ROLL = baked("dinner_roll", 3, 0.5F);
    public static final RegistryObject<Item> CORN_BREAD = baked("corn_bread", 5, 0.6F);
    public static final RegistryObject<Item> BAKED_CORN = baked("baked_corn", 3, 0.5F);
    public static final RegistryObject<Item> GARLIC_BAGUETTE = baked("garlic_baguette", 6, 0.6F);

    // ---- 装配类(工作台,基础档,dishes-and-gains.md §5):急迫 I 15 s 微效果 ----
    public static final RegistryObject<Item> VEGETABLE_SANDWICH = hand("vegetable_sandwich", 5, 0.4F);
    public static final RegistryObject<Item> EGG_SANDWICH = hand("egg_sandwich", 4, 0.4F);
    public static final RegistryObject<Item> BEEF_SANDWICH = hand("beef_sandwich", 7, 0.5F);
    public static final RegistryObject<Item> CHICKEN_SANDWICH = hand("chicken_sandwich", 6, 0.5F);
    public static final RegistryObject<Item> BEEF_BURGER = hand("beef_burger", 9, 0.6F);
    public static final RegistryObject<Item> CHICKEN_BURGER = hand("chicken_burger", 8, 0.6F);

    public static final List<RegistryObject<Item>> ALL = List.of(
            RICE_NOODLE_SOUP, TOMATO_SOUP, VEGETABLE_SOUP, RIB_SOUP, RICE_PORRIDGE,
            CORN_SOUP, DUMPLINGS,
            STEAMED_RICE, MANTOU, BAOZI, STEAMED_EGG, STEAMED_CORN, RICE_CAKE,
            RICE_STEAMED_PORK, WHITE_CAKE,
            TOMATO_SCRAMBLED_EGG, FRIED_RICE, FRIED_NOODLES, CHILI_SHREDDED_PORK,
            STIR_FRIED_PORK, KUNG_PAO_CHICKEN, STIR_FRIED_VEGETABLES, FRIED_RICE_NOODLES,
            BEEF_CHOW_FUN,
            FRIES, POTATO_CHIPS, FRIED_CHICKEN_CUTS, FRIED_PEANUTS, FRIED_FISH_FILLET,
            ONION_RINGS, SPRING_ROLL, RICE_CRACKER, CHICKEN_CUTLET, CHICKEN_TENDER,
            FRUIT_PLATTER, SUGAR_TOMATO, CHICKEN_COLD_NOODLES, FRIED_EGG, PLAIN_NOODLES,
            HAND_FRIED_RICE, HAND_STEAMED_EGG, HAND_STEAMED_CORN,
            BREAD, TOAST, SWEET_BREAD, CAKE, APPLE_PIE, BAGUETTE, DINNER_ROLL,
            CORN_BREAD, BAKED_CORN, GARLIC_BAGUETTE,
            VEGETABLE_SANDWICH, EGG_SANDWICH, BEEF_SANDWICH, CHICKEN_SANDWICH,
            BEEF_BURGER, CHICKEN_BURGER);

    private static RegistryObject<Item> dish(String name, FoodProperties food) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(food)));
    }

    // c1 煮:急迫 I 5 min + 生命恢复 I 30 s
    private static RegistryObject<Item> soup(String name, int hunger, float saturation) {
        return dish(name, food(hunger, saturation)
                .effect(haste(), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0), 1.0F)
                .build());
    }

    // c2 蒸:急迫 I 5 min + 伤害吸收 I 5 min
    private static RegistryObject<Item> steamed(String name, int hunger, float saturation) {
        return dish(name, food(hunger, saturation)
                .effect(haste(), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 6000, 0), 1.0F)
                .build());
    }

    // c3 炒:急迫 II 4 min(强度升档、时长微缩)
    private static RegistryObject<Item> stirfry(String name, int hunger, float saturation) {
        return dish(name, food(hunger, saturation)
                .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 4800, 1), 1.0F)
                .build());
    }

    // c4 炸:急迫 I 5 min + 速度 I 3 min
    private static RegistryObject<Item> fried(String name, int hunger, float saturation) {
        return dish(name, food(hunger, saturation)
                .effect(haste(), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 0), 1.0F)
                .build());
    }

    // c5 烤:急迫 I 5 min + 饱和(瞬时,40 tick)
    private static RegistryObject<Item> baked(String name, int hunger, float saturation) {
        return dish(name, food(hunger, saturation)
                .effect(haste(), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 40, 0), 1.0F)
                .build());
    }

    // 基础档:无主增益,仅急迫 I 15 s(300 tick)
    private static RegistryObject<Item> hand(String name, int hunger, float saturation) {
        return dish(name, food(hunger, saturation)
                .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 300, 0), 1.0F)
                .build());
    }

    private static FoodProperties.Builder food(int hunger, float saturation) {
        return new FoodProperties.Builder().nutrition(hunger).saturationMod(saturation);
    }

    private static Supplier<MobEffectInstance> haste() {
        return () -> new MobEffectInstance(MobEffects.DIG_SPEED, 6000, 0);
    }
}
