package net.mgear.gregfoodexpansion.cooking;

import java.util.List;
import java.util.function.Supplier;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.gains.GFEFoodItem;
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
 *
 * 增益多样性(§2.3 已定案 A+B):
 * - A 食材风味层:配方含对应食材即在模式效果外附加第三槽风味效果(I 级,≤2 min,
 *   各档通吃;番茄汤类与煮档再生同效,按 MobEffectInstance#update 合并语义不重复标注):
 *   辣椒系→力量 I 90 s / 鱼系→水下呼吸 I 2 min / 番茄系→再生 I 30 s / 枸杞→夜视(预留,枸杞未入菜);
 * - B 招牌菜独占(覆盖 A 同效果):宫保鸡丁 +力量 I 2 min、干炒牛河 +速度 I 1 min、米粉蒸肉 +饱和(瞬时)。
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
    public static final RegistryObject<Item> SHABU_BEEF = soup("shabu_beef", 6, 0.5F);
    public static final RegistryObject<Item> SHABU_MUTTON = soup("shabu_mutton", 6, 0.5F);
    public static final RegistryObject<Item> BEEF_RICE_BOWL = soup("beef_rice_bowl", 8, 0.6F);
    public static final RegistryObject<Item> TOMATO_EGG_SOUP = soup("tomato_egg_soup", 4, 0.4F);
    public static final RegistryObject<Item> BEEF_SOUP = soup("beef_soup", 5, 0.4F);
    public static final RegistryObject<Item> CHICKEN_SOUP = soup("chicken_soup", 5, 0.4F);
    public static final RegistryObject<Item> PEANUT_SOUP = soup("peanut_soup", 4, 0.5F);
    public static final RegistryObject<Item> GEDA_SOUP = soup("geda_soup", 5, 0.4F);
    public static final RegistryObject<Item> TOFU_SOUP = soup("tofu_soup", 4, 0.4F);
    public static final RegistryObject<Item> BRAISED_DRIED_TOFU = soup("braised_dried_tofu", 5, 0.6F);

    // ---- c2 蒸 ----
    public static final RegistryObject<Item> STEAMED_RICE = steamed("steamed_rice", 4, 0.4F);
    public static final RegistryObject<Item> MANTOU = steamed("mantou", 4, 0.4F);
    public static final RegistryObject<Item> BAOZI = steamed("baozi", 8, 0.5F);
    public static final RegistryObject<Item> STEAMED_EGG = steamed("steamed_egg", 4, 0.4F);
    public static final RegistryObject<Item> STEAMED_CORN = steamed("steamed_corn", 2, 0.3F);
    public static final RegistryObject<Item> RICE_CAKE = steamed("rice_cake", 5, 0.4F);
    public static final RegistryObject<Item> RICE_STEAMED_PORK = steamed("rice_steamed_pork", 10, 0.6F, instantSaturation());
    public static final RegistryObject<Item> WHITE_CAKE = steamed("white_cake", 5, 0.4F);
    public static final RegistryObject<Item> STEAMED_FISH = steamed("steamed_fish", 6, 0.5F, fishBreathing());
    public static final RegistryObject<Item> STEAMED_MEAT_PATTY = steamed("steamed_meat_patty", 6, 0.5F);

    // ---- c3 炒(急迫 II,大菜;招牌菜按 §2.3-B 附加独占风味) ----
    public static final RegistryObject<Item> TOMATO_SCRAMBLED_EGG = stirfry("tomato_scrambled_egg", 6, 0.6F, tomatoRegen());
    public static final RegistryObject<Item> FRIED_RICE = stirfry("fried_rice", 8, 0.6F);
    public static final RegistryObject<Item> FRIED_NOODLES = stirfry("fried_noodles", 8, 0.6F);
    public static final RegistryObject<Item> CHILI_SHREDDED_PORK = stirfry("chili_shredded_pork", 8, 0.6F, chiliPower());
    public static final RegistryObject<Item> STIR_FRIED_PORK = stirfry("stir_fried_pork", 9, 0.7F);
    public static final RegistryObject<Item> KUNG_PAO_CHICKEN = stirfry("kung_pao_chicken", 10, 0.7F, kungPaoPower());
    public static final RegistryObject<Item> STIR_FRIED_VEGETABLES = stirfry("stir_fried_vegetables", 5, 0.5F);
    public static final RegistryObject<Item> FRIED_RICE_NOODLES = stirfry("fried_rice_noodles", 8, 0.6F);
    public static final RegistryObject<Item> BEEF_CHOW_FUN = stirfry("beef_chow_fun", 10, 0.7F, wokSpeed());
    public static final RegistryObject<Item> TOMATO_BEEF = stirfry("tomato_beef", 7, 0.6F, tomatoRegen());
    public static final RegistryObject<Item> ONION_FRIED_LAMB = stirfry("onion_fried_lamb", 7, 0.6F);
    public static final RegistryObject<Item> CABBAGE_FRIED_PORK = stirfry("cabbage_fried_pork", 6, 0.5F);
    public static final RegistryObject<Item> MUXU_PORK = stirfry("muxu_pork", 7, 0.6F);
    public static final RegistryObject<Item> HOME_STYLE_TOFU = stirfry("home_style_tofu", 6, 0.6F);
    public static final RegistryObject<Item> DRIED_TOFU_PORK = stirfry("dried_tofu_pork", 7, 0.6F);
    public static final RegistryObject<Item> TOFU_SHEET_PORK = stirfry("tofu_sheet_pork", 7, 0.6F);
    // ---- 死面快手主食(2026-09-08):死面/面皮不经酵母闸门的菜肴(dough gate 定案) ----
    public static final RegistryObject<Item> SCALLION_PANCAKE = stirfry("scallion_pancake", 6, 0.5F);
    public static final RegistryObject<Item> FRIED_DUMPLINGS = stirfry("fried_dumplings", 7, 0.6F);

    // ---- c4 炸(高饱和) ----
    public static final RegistryObject<Item> FRIES = fried("fries", 5, 0.8F);
    public static final RegistryObject<Item> POTATO_CHIPS = fried("potato_chips", 3, 0.8F);
    public static final RegistryObject<Item> FRIED_CHICKEN_CUTS = fried("fried_chicken_cuts", 6, 0.8F);
    public static final RegistryObject<Item> FRIED_PEANUTS = fried("fried_peanuts", 3, 0.8F);
    public static final RegistryObject<Item> FRIED_FISH_FILLET = fried("fried_fish_fillet", 6, 0.8F, fishBreathing());
    public static final RegistryObject<Item> ONION_RINGS = fried("onion_rings", 4, 0.8F);
    public static final RegistryObject<Item> SPRING_ROLL = fried("spring_roll", 6, 0.8F);
    public static final RegistryObject<Item> RICE_CRACKER = fried("rice_cracker", 3, 0.8F);
    public static final RegistryObject<Item> PAN_FRIED_TOFU = fried("pan_fried_tofu", 5, 0.8F);
    public static final RegistryObject<Item> CHICKEN_CUTLET = fried("chicken_cutlet", 6, 0.7F);
    public static final RegistryObject<Item> CHICKEN_TENDER = fried("chicken_tender", 5, 0.7F);

    // ---- 烧烤口味串(烹饪机 c6,精制档烤口径:急迫 I 5min + 饱和瞬时) ----
    public static final RegistryObject<Item> GRILLED_LAMB_SKEWER = baked("grilled_lamb_skewer", 6, 0.6F);
    public static final RegistryObject<Item> GRILLED_BEEF_SKEWER = baked("grilled_beef_skewer", 6, 0.6F);
    public static final RegistryObject<Item> GRILLED_CHICKEN_SKEWER = baked("grilled_chicken_skewer", 5, 0.6F);
    public static final RegistryObject<Item> SALT_GRILLED_LAMB = baked("salt_grilled_lamb_skewer", 6, 0.6F);
    public static final RegistryObject<Item> SALT_GRILLED_BEEF = baked("salt_grilled_beef_skewer", 6, 0.6F);
    public static final RegistryObject<Item> SALT_GRILLED_CHICKEN = baked("salt_grilled_chicken_skewer", 5, 0.6F);
    public static final RegistryObject<Item> CHILI_GRILLED_LAMB = baked("chili_grilled_lamb_skewer", 6, 0.6F, chiliPower());
    public static final RegistryObject<Item> CHILI_GRILLED_BEEF = baked("chili_grilled_beef_skewer", 6, 0.6F, chiliPower());
    public static final RegistryObject<Item> CHILI_GRILLED_CHICKEN = baked("chili_grilled_chicken_skewer", 5, 0.6F, chiliPower());
    // 酱烤(生抽 20mB 涂刷;2026-09-08 大豆链落地后补,dishes-and-gains §5)
    public static final RegistryObject<Item> SOY_GRILLED_LAMB = baked("soy_grilled_lamb_skewer", 6, 0.6F);
    public static final RegistryObject<Item> SOY_GRILLED_BEEF = baked("soy_grilled_beef_skewer", 6, 0.6F);
    public static final RegistryObject<Item> SOY_GRILLED_CHICKEN = baked("soy_grilled_chicken_skewer", 5, 0.6F);
    // ---- 精制档补全(2026-09-08):营火辣椒串/营火鸡爪串的烹饪机对应版,与营火版同食材、精制档烤口径 ----
    public static final RegistryObject<Item> GRILLED_CHILI_SKEWER = baked("grilled_chili_skewer", 3, 0.5F, chiliPower());
    public static final RegistryObject<Item> GRILLED_CHICKEN_FEET_SKEWER = baked("grilled_chicken_feet_skewer", 3, 0.5F);

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

    // ---- 烧烤(2026-09-08):营火=基础档(急迫 I 15s);铁签串=精制档(烤口径:急迫 I 5min+饱和);风味层各档通吃(§2.3) ----
    public static final RegistryObject<Item> CAMPFIRE_LAMB_SKEWER = hand("campfire_lamb_skewer", 3, 0.3F);
    public static final RegistryObject<Item> CAMPFIRE_BEEF_SKEWER = hand("campfire_beef_skewer", 3, 0.3F);
    public static final RegistryObject<Item> CAMPFIRE_CHICKEN_SKEWER = hand("campfire_chicken_skewer", 3, 0.3F);
    public static final RegistryObject<Item> CAMPFIRE_CHILI_SKEWER = hand("campfire_chili_skewer", 2, 0.3F, chiliPower());
    public static final RegistryObject<Item> CAMPFIRE_CHICKEN_FEET_SKEWER = hand("campfire_chicken_feet_skewer", 2, 0.3F);
    public static final RegistryObject<Item> IRON_LAMB_SKEWER = baked("iron_lamb_skewer", 7, 0.6F);
    public static final RegistryObject<Item> IRON_BEEF_ROLL_SKEWER = baked("iron_beef_roll_skewer", 7, 0.6F);
    public static final RegistryObject<Item> IRON_BEEF_SKEWER = baked("iron_beef_skewer", 8, 0.7F);
    public static final RegistryObject<Item> GRILLED_FISH = baked("grilled_fish", 6, 0.6F, fishBreathing());

    // ---- 发酵小菜(发酵槽产出,精制档主效果;佐餐小菜兼未来配料,dishes-and-gains §5) ----
    public static final RegistryObject<Item> PICKLED_CABBAGE = pickle("pickled_cabbage", 2, 0.3F);

    // ---- 死面烤制(c5):牛肉馅饼 ----
    public static final RegistryObject<Item> BEEF_PIE = baked("beef_pie", 8, 0.6F);

    public static final List<RegistryObject<Item>> ALL = List.of(
            RICE_NOODLE_SOUP, TOMATO_SOUP, VEGETABLE_SOUP, RIB_SOUP, RICE_PORRIDGE,
            CORN_SOUP, DUMPLINGS, SHABU_BEEF, SHABU_MUTTON, BEEF_RICE_BOWL,
            TOMATO_EGG_SOUP, BEEF_SOUP, CHICKEN_SOUP, PEANUT_SOUP, GEDA_SOUP,
            STEAMED_RICE, MANTOU, BAOZI, STEAMED_EGG, STEAMED_CORN, RICE_CAKE,
            RICE_STEAMED_PORK, WHITE_CAKE, STEAMED_FISH, STEAMED_MEAT_PATTY,
            TOMATO_SCRAMBLED_EGG, FRIED_RICE, FRIED_NOODLES, CHILI_SHREDDED_PORK,
            STIR_FRIED_PORK, KUNG_PAO_CHICKEN, STIR_FRIED_VEGETABLES, FRIED_RICE_NOODLES,
            BEEF_CHOW_FUN, TOMATO_BEEF, ONION_FRIED_LAMB, CABBAGE_FRIED_PORK, MUXU_PORK,
            SCALLION_PANCAKE, FRIED_DUMPLINGS,
            FRIES, POTATO_CHIPS, FRIED_CHICKEN_CUTS, FRIED_PEANUTS, FRIED_FISH_FILLET,
            ONION_RINGS, SPRING_ROLL, RICE_CRACKER, CHICKEN_CUTLET, CHICKEN_TENDER,
            FRUIT_PLATTER, SUGAR_TOMATO, CHICKEN_COLD_NOODLES, FRIED_EGG, PLAIN_NOODLES,
            HAND_FRIED_RICE, HAND_STEAMED_EGG, HAND_STEAMED_CORN,
            BREAD, TOAST, SWEET_BREAD, CAKE, APPLE_PIE, BAGUETTE, DINNER_ROLL,
            CORN_BREAD, BAKED_CORN, GARLIC_BAGUETTE, GRILLED_FISH,
            VEGETABLE_SANDWICH, EGG_SANDWICH, BEEF_SANDWICH, CHICKEN_SANDWICH,
            BEEF_BURGER, CHICKEN_BURGER,
            CAMPFIRE_LAMB_SKEWER, CAMPFIRE_BEEF_SKEWER, CAMPFIRE_CHICKEN_SKEWER,
            CAMPFIRE_CHILI_SKEWER, CAMPFIRE_CHICKEN_FEET_SKEWER,
            IRON_LAMB_SKEWER, IRON_BEEF_ROLL_SKEWER, IRON_BEEF_SKEWER,
            GRILLED_LAMB_SKEWER, GRILLED_BEEF_SKEWER, GRILLED_CHICKEN_SKEWER,
            SALT_GRILLED_LAMB, SALT_GRILLED_BEEF, SALT_GRILLED_CHICKEN,
            CHILI_GRILLED_LAMB, CHILI_GRILLED_BEEF, CHILI_GRILLED_CHICKEN,
            SOY_GRILLED_LAMB, SOY_GRILLED_BEEF, SOY_GRILLED_CHICKEN,
            GRILLED_CHILI_SKEWER, GRILLED_CHICKEN_FEET_SKEWER,
            BEEF_PIE,
            PICKLED_CABBAGE);

    private static RegistryObject<Item> dish(String name, FoodProperties.Builder builder,
                                             Supplier<MobEffectInstance>[] flavor) {
        // GFEFoodItem:进食后经增益结算管线(dishes-and-gains.md §2.1)升级效果时长
        for (Supplier<MobEffectInstance> extra : flavor) {
            builder = builder.effect(extra, 1.0F);
        }
        FoodProperties.Builder settled = builder;
        return ITEMS.register(name, () -> new GFEFoodItem(new Item.Properties().food(settled.build())));
    }

    // c1 煮:急迫 I 5 min + 生命恢复 I 30 s
    @SafeVarargs
    private static RegistryObject<Item> soup(String name, int hunger, float saturation,
                                             Supplier<MobEffectInstance>... flavor) {
        return dish(name, food(hunger, saturation)
                .effect(haste(), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0), 1.0F), flavor);
    }

    // c2 蒸:急迫 I 5 min + 伤害吸收 I 5 min
    @SafeVarargs
    private static RegistryObject<Item> steamed(String name, int hunger, float saturation,
                                                Supplier<MobEffectInstance>... flavor) {
        return dish(name, food(hunger, saturation)
                .effect(haste(), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 6000, 0), 1.0F), flavor);
    }

    // c3 炒:急迫 II 4 min(强度升档、时长微缩)
    @SafeVarargs
    private static RegistryObject<Item> stirfry(String name, int hunger, float saturation,
                                                Supplier<MobEffectInstance>... flavor) {
        return dish(name, food(hunger, saturation)
                .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 4800, 1), 1.0F), flavor);
    }

    // c4 炸:急迫 I 5 min + 速度 I 3 min
    @SafeVarargs
    private static RegistryObject<Item> fried(String name, int hunger, float saturation,
                                              Supplier<MobEffectInstance>... flavor) {
        return dish(name, food(hunger, saturation)
                .effect(haste(), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 0), 1.0F), flavor);
    }

    // c5 烤:急迫 I 5 min + 饱和(瞬时,40 tick)
    @SafeVarargs
    private static RegistryObject<Item> baked(String name, int hunger, float saturation,
                                              Supplier<MobEffectInstance>... flavor) {
        return dish(name, food(hunger, saturation)
                .effect(haste(), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 40, 0), 1.0F), flavor);
    }

    // 基础档:无主增益,仅急迫 I 15 s(300 tick)
    @SafeVarargs
    private static RegistryObject<Item> hand(String name, int hunger, float saturation,
                                             Supplier<MobEffectInstance>... flavor) {
        return dish(name, food(hunger, saturation)
                .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 300, 0), 1.0F), flavor);
    }

    // 发酵小菜:急迫 I 5 min,无副效果(低营养佐餐;发酵槽产出=精制档,dishes-and-gains §2)
    private static RegistryObject<Item> pickle(String name, int hunger, float saturation) {
        return dish(name, food(hunger, saturation).effect(haste(), 1.0F), new Supplier[0]);
    }

    private static FoodProperties.Builder food(int hunger, float saturation) {
        return new FoodProperties.Builder().nutrition(hunger).saturationMod(saturation);
    }

    private static Supplier<MobEffectInstance> haste() {
        return () -> new MobEffectInstance(MobEffects.DIG_SPEED, 6000, 0);
    }

    // ---- §2.3-A 食材风味层(I 级,≤2 min;§2.1 管线照常只乘时长,瞬时效果被管线跳过) ----

    /** 辣椒系→力量 I 90 s(辣劲上头;力量全模组唯一入菜轴,短时效压战斗影响)。 */
    private static Supplier<MobEffectInstance> chiliPower() {
        return () -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800, 0);
    }

    /** 鱼系→水下呼吸 I 2 min(靠海吃海)。 */
    private static Supplier<MobEffectInstance> fishBreathing() {
        return () -> new MobEffectInstance(MobEffects.WATER_BREATHING, 2400, 0);
    }

    /** 番茄系→再生 I 30 s(与煮档副效果同效,MobEffectInstance#update 合并语义天然兼容)。 */
    private static Supplier<MobEffectInstance> tomatoRegen() {
        return () -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0);
    }

    /** 枸杞→夜视 8 min:预留(枸杞尚未入菜,入菜时按 §3 语义启用)。 */
    private static Supplier<MobEffectInstance> gojiNightVision() {
        return () -> new MobEffectInstance(MobEffects.NIGHT_VISION, 9600, 0);
    }

    // ---- §2.3-B 招牌菜独占风味(覆盖 A 的同效果映射,不重复标注) ----

    /** 宫保鸡丁(辣椒+花生)→力量 I 2 min:覆盖 A 的辣椒力量 90 s。 */
    private static Supplier<MobEffectInstance> kungPaoPower() {
        return () -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4800, 0);
    }

    /** 干炒牛河→速度 I 1 min(锅气)。 */
    private static Supplier<MobEffectInstance> wokSpeed() {
        return () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0);
    }

    /** 米粉蒸肉→饱和(瞬时):米粉裹肉管饱;瞬时效果不参与 §2.1 时长管线。 */
    private static Supplier<MobEffectInstance> instantSaturation() {
        return () -> new MobEffectInstance(MobEffects.SATURATION, 40, 0);
    }
}
