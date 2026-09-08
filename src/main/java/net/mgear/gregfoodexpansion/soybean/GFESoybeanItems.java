package net.mgear.gregfoodexpansion.soybean;

import java.util.List;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.gains.GFEFoodItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 大豆链物品(soybean-chain.md §2,已定案注册表 + 实现期新增 1 项):
 * 麸皮/煮大豆/曲/种曲/豆渣/豆腐×2/豆腐脑/豆粕/酱渣;
 * koji_batch(曲料)为实现期新增——发酵槽配方 IO 1/1/1/1(GTRecipeTypes.java:272)
 * 塞不下"煮大豆+麸皮+种曲"三原料,拌曲(搅拌机)预混为曲料后再入发酵槽制曲。
 * 流体(豆浆/豆油/发酵醪/生抽/老抽/焦糖糖色/盐水/卤水)走 GTFEMaterials 材料。
 * 豆腐为中间品食材,直接食用按基础档数值、无品质标记(soybean-chain.md §5)。
 */
public final class GFESoybeanItems {
    private GFESoybeanItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GregFoodExpansion.MOD_ID);

    // ---- 链条中间品 ----
    public static final RegistryObject<Item> BRAN = form("bran");
    public static final RegistryObject<Item> COOKED_SOYBEAN = form("cooked_soybean");
    public static final RegistryObject<Item> KOJI = form("koji");
    public static final RegistryObject<Item> KOJI_STARTER = form("koji_starter");
    public static final RegistryObject<Item> KOJI_BATCH = form("koji_batch");
    public static final RegistryObject<Item> OKARA = form("okara");
    public static final RegistryObject<Item> SOYBEAN_MEAL = form("soybean_meal");
    public static final RegistryObject<Item> SOYBEAN_POMACE = form("soybean_pomace");
    // ---- 豆腐体系(基础档可直接食用) ----
    public static final RegistryObject<Item> FIRM_TOFU = tofu("firm_tofu", 2, 0.3F);
    public static final RegistryObject<Item> SOFT_TOFU = tofu("soft_tofu", 2, 0.3F);
    public static final RegistryObject<Item> TOFU_PUDDING = tofu("tofu_pudding", 3, 0.4F);
    // 千叶豆腐:大豆蛋白+粉体混配,冷冻定型;qianye_tofu_blank 为冷冻前坯(不可食)。
    public static final RegistryObject<Item> QIANYE_TOFU_BLANK = form("qianye_tofu_blank");
    public static final RegistryObject<Item> QIANYE_TOFU = tofu("qianye_tofu", 3, 0.35F);
    // 豆干(北豆腐压制脱水)/千张(点卤浆料压延成薄层):2026-09-08 追加
    public static final RegistryObject<Item> DRIED_TOFU = tofu("dried_tofu", 4, 0.5F);
    public static final RegistryObject<Item> TOFU_SHEET_BLANK = form("tofu_sheet_blank");
    public static final RegistryObject<Item> TOFU_SHEET = tofu("tofu_sheet", 3, 0.4F);

    public static final List<RegistryObject<Item>> ALL = List.of(
            BRAN, COOKED_SOYBEAN, KOJI, KOJI_STARTER, KOJI_BATCH,
            OKARA, SOYBEAN_MEAL, SOYBEAN_POMACE,
            FIRM_TOFU, SOFT_TOFU, TOFU_PUDDING,
            QIANYE_TOFU_BLANK, QIANYE_TOFU,
            DRIED_TOFU, TOFU_SHEET_BLANK, TOFU_SHEET);

    private static RegistryObject<Item> form(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    // 豆腐/豆腐脑:基础档食材(soybean-chain.md §5),无增益;GFEFoodItem 使其参与
    // 增益管线(当前乘数为 1.0 直通,后续加基础档急迫效果时自动生效)。
    private static RegistryObject<Item> tofu(String name, int hunger, float saturation) {
        return ITEMS.register(name, () -> new GFEFoodItem(new Item.Properties().food(
                new FoodProperties.Builder()
                        .nutrition(hunger)
                        .saturationMod(saturation)
                        .build())));
    }
}
