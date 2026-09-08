package net.mgear.gregfoodexpansion.alcohol;

import java.util.List;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 酒线物品(alcohol-line.md,2026-09-08 定案实装):
 * 酵母(自举闸门)/麦芽(大麦烤一档)/瓶装酒 ×9(发酵酒 6 + 蒸馏酒 3,米酒黄酒分线)。
 * 流体走 GTFEMaterials 材料(两汁/麦芽汁/稀糖水/两米醪 + 9 酒液)。
 *
 * 酒精效果(alcohol-line.md §4,M1 微醺轴,效果经 GFEFoodItem 进 §2.1 管线):
 * - 轻醇档:幸运 I 3600t(3min)——啤酒/苹果酒/蜂蜜酒/米酒(甜型低度);
 * - 醇厚档:幸运 I 6000t(5min)+ 跳跃提升 I 1200t(1min)——葡萄酒/黄酒/蒸馏酒 3。
 * 幸运/跳跃为菜肴未占用轴(dishes-and-gains.md §2.3 错位核对通过)。
 * 饮用属性:营养 1(米酒 2)、alwaysEdible(酒不顶饱,满饥饿也可饮)、stacksTo 16、还玻璃瓶。
 */
public final class GFEAlcoholItems {
    private GFEAlcoholItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GregFoodExpansion.MOD_ID);

    // ---- 链条中间品 ----
    public static final RegistryObject<Item> YEAST = form("yeast");
    public static final RegistryObject<Item> MALT = form("malt");

    // ---- 瓶装酒:轻醇档(幸运 I 3min) ----
    public static final RegistryObject<Item> BOTTLED_BEER = bottle("bottled_beer", light());
    public static final RegistryObject<Item> BOTTLED_CIDER = bottle("bottled_cider", light());
    public static final RegistryObject<Item> BOTTLED_MEAD = bottle("bottled_mead", light());
    // 米酒:甜型含米,营养 2;轻醇档效果
    public static final RegistryObject<Item> BOTTLED_RICE_WINE = bottle("bottled_rice_wine", riceWine());
    // ---- 瓶装酒:醇厚档(幸运 I 5min + 跳跃提升 I 1min) ----
    public static final RegistryObject<Item> BOTTLED_WINE = bottle("bottled_wine", full());
    public static final RegistryObject<Item> BOTTLED_HUANGJIU = bottle("bottled_huangjiu", full());
    public static final RegistryObject<Item> BOTTLED_BRANDY = bottle("bottled_brandy", full());
    public static final RegistryObject<Item> BOTTLED_WHISKY = bottle("bottled_whisky", full());
    public static final RegistryObject<Item> BOTTLED_BAIJIU = bottle("bottled_baijiu", full());

    public static final List<RegistryObject<Item>> ALL = List.of(
            YEAST, MALT,
            BOTTLED_BEER, BOTTLED_CIDER, BOTTLED_MEAD, BOTTLED_RICE_WINE,
            BOTTLED_WINE, BOTTLED_HUANGJIU, BOTTLED_BRANDY, BOTTLED_WHISKY, BOTTLED_BAIJIU);

    private static RegistryObject<Item> form(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    /** 幸运 I 3600t:轻醇档(啤酒/苹果酒/蜂蜜酒/米酒)。 */
    private static FoodProperties light() {
        return new FoodProperties.Builder()
                .nutrition(1)
                .saturationMod(0.1F)
                .alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.LUCK, 3600, 0), 1.0F)
                .build();
    }

    /** 米酒变体:营养 2/饱和 0.2,效果同轻醇档。 */
    private static FoodProperties riceWine() {
        return new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.2F)
                .alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.LUCK, 3600, 0), 1.0F)
                .build();
    }

    /** 幸运 I 6000t + 跳跃提升 I 1200t:醇厚档(葡萄酒/黄酒/蒸馏酒)。 */
    private static FoodProperties full() {
        return new FoodProperties.Builder()
                .nutrition(1)
                .saturationMod(0.1F)
                .alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.LUCK, 6000, 0), 1.0F)
                .effect(() -> new MobEffectInstance(MobEffects.JUMP, 1200, 0), 1.0F)
                .build();
    }

    private static RegistryObject<Item> bottle(String name, FoodProperties food) {
        return ITEMS.register(name, () -> new GFEBottleItem(new Item.Properties()
                .food(food)
                .stacksTo(16)
                .craftRemainder(Items.GLASS_BOTTLE)));
    }
}
