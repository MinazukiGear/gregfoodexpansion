package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.cooking.GFEDishes;
import net.mgear.gregfoodexpansion.registry.GFERecipeTypes;
import net.mgear.gregfoodexpansion.registry.GTFEMaterials;
import net.mgear.gregfoodexpansion.prep.GFEFormItems;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.mgear.gregfoodexpansion.soybean.GFESoybeanItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

/**
 * 通用烹饪机电路配方(universal-cooker.md §3/§4/§5 草案数值,基准;
 * 菜肴名录 dishes-and-gains.md §5,M1 原料可达 32 道):
 * c1 煮(水/奶介质)/ c2 蒸 / c3 炒(食用油 20 mB)/ c4 炸(油浴装填 1,000 mB、回收后净吸收 100-200 mB)。
 * 用油口径(2026-09-08 现实化调整,1 mB = 1 mL):炒按每盘 15-30 mL 取 20;炸为油浴操作,
 * 配方按"装填-回收"建模——每批输入 1 桶油浴、输出扣除吸收后的回油(净吸收:裸炸 100 /
 * 挂糊 150 / 重糊 200),回油可回流复用;吸收量即净油耗,装填量即机器油浴吞吐。
 * 食用油口径(2026-09-08 迁移完成):炒/炸/煎按 `#forge:cooking_oil` 标签取油
 * (GTCEu 种子油 + 本模组豆油均在标签内),不再写死 SeedOil。
 * 经 IGTAddon#addRecipes 走 GTCEu 动态数据包注册。
 */
public final class GFECookingRecipes {
    private GFECookingRecipes() {}

    // ② 层 forge 形态标签:任意肉丝(compatibility-boundary.md §3)
    /** 炒菜用油:现实一盘炒菜约 15-30 mL(1 mB = 1 mL)。 */
    private static final int STIRFRY_OIL = 20;

    /** 食用油标签(2026-09-08 迁移):豆油已入 #forge:cooking_oil,炒/炸/煎全模式按标签取油。 */
    private static final TagKey<Fluid> COOKING_OIL = TagKey.create(Registries.FLUID,
            ResourceLocation.fromNamespaceAndPath("forge", "cooking_oil"));

    private static final TagKey<Item> ANY_MEAT_STRIPS = TagKey.create(net.minecraft.core.registries.Registries.ITEM,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("forge", "meat_strips"));

    public static void init(Consumer<FinishedRecipe> provider) {
        // ---- c1 煮:200-400 tick / 8-12 EU/t ----
        soup(provider, "rice_noodle_soup", 300, 8, b -> b
                .inputItems(GFEFormItems.RICE_NOODLES.get()).inputFluids(GTMaterials.Water, 200)
                .outputItems(GFEDishes.RICE_NOODLE_SOUP.get()));
        soup(provider, "tomato_soup", 200, 8, b -> b
                .inputItems(GFEFormItems.TOMATO_DICED.get(), 2).inputFluids(GTMaterials.Water, 250)
                .outputItems(GFEDishes.TOMATO_SOUP.get()));
        soup(provider, "vegetable_soup", 200, 8, b -> b
                .inputItems(GFEFormItems.CABBAGE_STRIP.get()).inputItems(Items.CARROT)
                .inputFluids(GTMaterials.Water, 250)
                .outputItems(GFEDishes.VEGETABLE_SOUP.get()));
        soup(provider, "pork_rib_soup", 400, 12, b -> b
                .inputItems(GFEFormItems.PORK_RIBS.get()).inputFluids(GTMaterials.Water, 250)
                .outputItems(GFEDishes.RIB_SOUP.get()));
        soup(provider, "beef_rib_soup", 400, 12, b -> b
                .inputItems(GFEFormItems.BEEF_RIBS.get()).inputFluids(GTMaterials.Water, 250)
                .outputItems(GFEDishes.RIB_SOUP.get()));
        soup(provider, "rice_porridge", 300, 8, b -> b
                .inputItems(GFECropItems.RICE.get(), 2).inputFluids(GTMaterials.Water, 250)
                .outputItems(GFEDishes.RICE_PORRIDGE.get()));
        soup(provider, "corn_soup", 300, 12, b -> b
                .inputItems(GFECropItems.CORN.get(), 2).inputFluids(GTMaterials.Milk, 250)
                .outputItems(GFEDishes.CORN_SOUP.get()));
        soup(provider, "dumplings", 300, 12, b -> b
                .inputItems(GFEFormItems.DOUGH_SHEET.get())
                .inputItems(ANY_MEAT_STRIPS, 2)
                .inputFluids(GTMaterials.Water, 200)
                .outputItems(GFEDishes.DUMPLINGS.get(), 2));
        soup(provider, "shabu_beef", 300, 8, b -> b
                .inputItems(GFEFormItems.FATTY_BEEF_ROLL.get())
                .inputFluids(GTMaterials.Water, 200)
                .outputItems(GFEDishes.SHABU_BEEF.get()));
        soup(provider, "shabu_mutton", 300, 8, b -> b
                .inputItems(GFEFormItems.FATTY_MUTTON_ROLL.get())
                .inputFluids(GTMaterials.Water, 200)
                .outputItems(GFEDishes.SHABU_MUTTON.get()));
        soup(provider, "tomato_egg_soup", 200, 8, b -> b
                .inputItems(GFEFormItems.TOMATO_DICED.get()).inputItems(Items.EGG)
                .inputFluids(GTMaterials.Water, 200)
                .outputItems(GFEDishes.TOMATO_EGG_SOUP.get()));
        soup(provider, "beef_soup", 300, 8, b -> b
                .inputItems(GFEFormItems.BEEF_SLICE.get()).inputFluids(GTMaterials.Water, 250)
                .outputItems(GFEDishes.BEEF_SOUP.get()));
        soup(provider, "chicken_soup", 300, 8, b -> b
                .inputItems(Items.CHICKEN).inputFluids(GTMaterials.Water, 250)
                .outputItems(GFEDishes.CHICKEN_SOUP.get()));
        soup(provider, "peanut_soup", 300, 8, b -> b
                .inputItems(GFECropItems.PEANUT.get(), 2).inputItems(Items.SUGAR)
                .inputFluids(GTMaterials.Water, 200)
                .outputItems(GFEDishes.PEANUT_SOUP.get()));
        soup(provider, "geda_soup", 200, 8, b -> b
                .inputItems(GFEFormItems.DOUGH_SHEET.get()).inputItems(GFEFormItems.TOMATO_DICED.get())
                .inputItems(Items.EGG).inputFluids(GTMaterials.Water, 200)
                .outputItems(GFEDishes.GEDA_SOUP.get()));
        // 豆腐汤(南豆腐嫩质,蛋花):soybean-chain.md 菜肴联动
        soup(provider, "tofu_soup", 200, 8, b -> b
                .inputItems(GFEFormItems.TOFU_CUBE.get()).inputItems(Items.EGG)
                .inputFluids(GTMaterials.Water, 200)
                .outputItems(GFEDishes.TOFU_SOUP.get()));
        // 卤豆干(生抽卤制入味,消费生抽闭环):soybean-chain.md 菜肴联动
        soup(provider, "braised_dried_tofu", 300, 8, b -> b
                .inputItems(GFESoybeanItems.DRIED_TOFU.get())
                .inputFluids(GTFEMaterials.SOY_SAUCE.getFluid(50))
                .outputItems(GFEDishes.BRAISED_DRIED_TOFU.get()));
        soup(provider, "beef_rice_bowl", 400, 12, b -> b
                .inputItems(GFEDishes.STEAMED_RICE.get()).inputItems(GFEFormItems.FATTY_BEEF_ROLL.get())
                .inputItems(GFEFormItems.ONION_DICED.get())
                .outputItems(GFEDishes.BEEF_RICE_BOWL.get()));

        // ---- c2 蒸:200-300 tick / 12-16 EU/t ----
        steam(provider, "steamed_rice", 200, 12, b -> b
                .inputItems(GFECropItems.RICE.get()).inputFluids(GTMaterials.Water, 100)
                .outputItems(GFEDishes.STEAMED_RICE.get()));
        steam(provider, "mantou", 200, 12, b -> b
                .inputItems(GFEFormItems.LEAVENED_DOUGH.get())
                .outputItems(GFEDishes.MANTOU.get(), 2));
        steam(provider, "baozi", 250, 12, b -> b
                .inputItems(GFEFormItems.DOUGH_SHEET.get()).inputItems(GFEFormItems.PORK_MINCED.get())
                .inputItems(GFEFormItems.ONION_DICED.get())
                .outputItems(GFEDishes.BAOZI.get(), 2));
        steam(provider, "steamed_egg", 200, 12, b -> b
                .inputItems(Items.EGG, 2).inputFluids(GTMaterials.Milk, 100)
                .outputItems(GFEDishes.STEAMED_EGG.get()));
        steam(provider, "steamed_corn", 200, 12, b -> b
                .inputItems(GFECropItems.CORN.get(), 2)
                .outputItems(GFEDishes.STEAMED_CORN.get(), 2));
        steam(provider, "rice_cake", 250, 12, b -> b
                .inputItems(GFEFormItems.RICE_FLOUR.get(), 2).inputItems(Items.SUGAR)
                .outputItems(GFEDishes.RICE_CAKE.get(), 2));
        steam(provider, "rice_steamed_pork", 300, 16, b -> b
                .inputItems(GFEFormItems.PORK_SLICE.get(), 2).inputItems(GFEFormItems.RICE_FLOUR.get())
                .outputItems(GFEDishes.RICE_STEAMED_PORK.get()));
        steam(provider, "white_cake", 300, 12, b -> b
                .inputItems(GFEFormItems.RICE_FLOUR.get(), 3).inputItems(Items.SUGAR, 2)
                .outputItems(GFEDishes.WHITE_CAKE.get()));
        steam(provider, "steamed_fish", 240, 12, b -> b
                .inputItems(GFEFormItems.FISH_CUBE.get())
                .outputItems(GFEDishes.STEAMED_FISH.get()));
        steam(provider, "steamed_meat_patty", 240, 12, b -> b
                .inputItems(GFEFormItems.CHICKEN_MINCED.get()).inputItems(Items.EGG)
                .outputItems(GFEDishes.STEAMED_MEAT_PATTY.get()));

        // ---- c3 炒:200-250 tick / 16-20 EU/t + 食用油 10 mB ----
        stirfry(provider, "tomato_scrambled_egg", 200, 16, b -> b
                .inputItems(GFEFormItems.TOMATO_DICED.get()).inputItems(Items.EGG)
                .outputItems(GFEDishes.TOMATO_SCRAMBLED_EGG.get()));
        stirfry(provider, "fried_rice", 200, 16, b -> b
                .inputItems(GFECropItems.RICE.get()).inputItems(ANY_MEAT_STRIPS)
                .outputItems(GFEDishes.FRIED_RICE.get()));
        stirfry(provider, "fried_noodles", 200, 16, b -> b
                .inputItems(GFEFormItems.NOODLE.get()).inputItems(ANY_MEAT_STRIPS)
                .outputItems(GFEDishes.FRIED_NOODLES.get()));
        stirfry(provider, "chili_shredded_pork", 200, 16, b -> b
                .inputItems(GFEFormItems.PORK_STRIP.get()).inputItems(GFECropItems.CHILI.get())
                .outputItems(GFEDishes.CHILI_SHREDDED_PORK.get()));
        stirfry(provider, "stir_fried_pork", 200, 20, b -> b
                .inputItems(GFEFormItems.PORK_SLICE.get()).inputItems(GFECropItems.CHILI.get())
                .outputItems(GFEDishes.STIR_FRIED_PORK.get()));
        stirfry(provider, "kung_pao_chicken", 250, 20, b -> b
                .inputItems(GFEFormItems.CHICKEN_DICED.get()).inputItems(GFECropItems.PEANUT.get())
                .inputItems(GFECropItems.CHILI.get())
                .outputItems(GFEDishes.KUNG_PAO_CHICKEN.get()));
        stirfry(provider, "stir_fried_vegetables", 200, 16, b -> b
                .inputItems(GFEFormItems.CABBAGE_STRIP.get()).inputItems(Items.CARROT)
                .outputItems(GFEDishes.STIR_FRIED_VEGETABLES.get()));
        stirfry(provider, "fried_rice_noodles", 200, 16, b -> b
                .inputItems(GFEFormItems.RICE_NOODLES.get()).inputItems(Items.EGG)
                .outputItems(GFEDishes.FRIED_RICE_NOODLES.get()));
        stirfry(provider, "beef_chow_fun", 250, 20, b -> b
                .inputItems(GFEFormItems.RICE_NOODLES.get()).inputItems(GFEFormItems.BEEF_SLICE.get())
                .inputItems(GFEFormItems.CABBAGE_STRIP.get())
                .outputItems(GFEDishes.BEEF_CHOW_FUN.get()));
        stirfry(provider, "tomato_beef", 200, 16, b -> b
                .inputItems(GFEFormItems.TOMATO_DICED.get()).inputItems(GFEFormItems.BEEF_SLICE.get())
                .outputItems(GFEDishes.TOMATO_BEEF.get()));
        stirfry(provider, "onion_fried_lamb", 200, 16, b -> b
                .inputItems(GFEFormItems.ONION_DICED.get()).inputItems(GFEFormItems.MUTTON_SLICE.get())
                .outputItems(GFEDishes.ONION_FRIED_LAMB.get()));
        stirfry(provider, "cabbage_fried_pork", 200, 16, b -> b
                .inputItems(GFEFormItems.CABBAGE_STRIP.get()).inputItems(GFEFormItems.PORK_SLICE.get())
                .outputItems(GFEDishes.CABBAGE_FRIED_PORK.get()));
        stirfry(provider, "muxu_pork", 240, 20, b -> b
                .inputItems(GFEFormItems.PORK_STRIP.get()).inputItems(Items.EGG)
                .outputItems(GFEDishes.MUXU_PORK.get()));
        // 家常豆腐(豆腐块+辣椒丝):soybean-chain.md 菜肴联动,#forge:tofu 形态形态入口
        stirfry(provider, "home_style_tofu", 200, 16, b -> b
                .inputItems(GFEFormItems.TOFU_CUBE.get()).inputItems(GFEFormItems.CHILI_STRIP.get())
                .outputItems(GFEDishes.HOME_STYLE_TOFU.get()));
        // 豆干炒肉(豆干+猪肉丝):soybean-chain.md 菜肴联动
        stirfry(provider, "dried_tofu_pork", 200, 16, b -> b
                .inputItems(GFESoybeanItems.DRIED_TOFU.get()).inputItems(GFEFormItems.PORK_STRIP.get())
                .outputItems(GFEDishes.DRIED_TOFU_PORK.get()));
        // 千张肉丝(千张+猪肉丝):soybean-chain.md 菜肴联动
        stirfry(provider, "tofu_sheet_pork", 240, 16, b -> b
                .inputItems(GFESoybeanItems.TOFU_SHEET.get()).inputItems(GFEFormItems.PORK_STRIP.get())
                .outputItems(GFEDishes.TOFU_SHEET_PORK.get()));
        // ---- 死面菜肴(2026-09-08):死面/面皮不经酵母闸门的快手主食 ----
        // 葱油饼:死面直接烙(死面在 c3 的独占消费点;原版面包含糖已禁用,见 IGTAddon#removeRecipes)
        stirfry(provider, "scallion_pancake", 200, 16, b -> b
                .inputItems(GTItems.DOUGH).inputItems(GFEFormItems.ONION_MINCED.get())
                .outputItems(GFEDishes.SCALLION_PANCAKE.get(), 2));
        // 煎饺:与水饺(面皮+肉丝+水煮)同料异法
        stirfry(provider, "fried_dumplings", 200, 16, b -> b
                .inputItems(GFEFormItems.DOUGH_SHEET.get()).inputItems(ANY_MEAT_STRIPS)
                .outputItems(GFEDishes.FRIED_DUMPLINGS.get(), 2));

        // ---- c4 炸:160-200 tick / 24-28 EU/t + 食用油 20-30 mB ----
        fry(provider, "fries", 160, 24, 100, b -> b
                .inputItems(GFEFormItems.FRIES_BLANK.get())
                .outputItems(GFEDishes.FRIES.get()));
        fry(provider, "potato_chips", 160, 24, 100, b -> b
                .inputItems(GFEFormItems.POTATO_SLICE.get(), 2)
                .outputItems(GFEDishes.POTATO_CHIPS.get(), 2));
        fry(provider, "fried_chicken_cuts", 200, 28, 150, b -> b
                .inputItems(GFEFormItems.CHICKEN_CUTS.get())
                .outputItems(GFEDishes.FRIED_CHICKEN_CUTS.get()));
        fry(provider, "fried_peanuts", 160, 24, 100, b -> b
                .inputItems(GFECropItems.PEANUT.get(), 2)
                .outputItems(GFEDishes.FRIED_PEANUTS.get(), 2));
        fry(provider, "fried_fish_fillet", 200, 28, 200, b -> b
                .inputItems(GFEFormItems.FISH_SLICE.get())
                .outputItems(GFEDishes.FRIED_FISH_FILLET.get()));
        fry(provider, "onion_rings", 160, 24, 150, b -> b
                .inputItems(GFECropItems.ONION.get())
                .outputItems(GFEDishes.ONION_RINGS.get(), 2));
        fry(provider, "spring_roll", 200, 28, 150, b -> b
                .inputItems(GFEFormItems.DOUGH_SHEET.get()).inputItems(GFEFormItems.CABBAGE_STRIP.get())
                .outputItems(GFEDishes.SPRING_ROLL.get(), 2));
        fry(provider, "rice_cracker", 160, 24, 100, b -> b
                .inputItems(GFEFormItems.RICE_FLOUR.get())
                .outputItems(GFEDishes.RICE_CRACKER.get(), 2));
        // 香煎豆腐(裸炸口径 100 mB 净油):soybean-chain.md 菜肴联动
        fry(provider, "pan_fried_tofu", 200, 24, 100, b -> b
                .inputItems(GFEFormItems.TOFU_CUBE.get(), 2)
                .outputItems(GFEDishes.PAN_FRIED_TOFU.get()));
        fry(provider, "chicken_cutlet", 200, 28, 150, b -> b
                .inputItems(GFEFormItems.RAW_CHICKEN_CUTLET.get())
                .outputItems(GFEDishes.CHICKEN_CUTLET.get()));
        fry(provider, "chicken_tender", 160, 24, 150, b -> b
                .inputItems(GFEFormItems.RAW_CHICKEN_TENDER.get(), 2)
                .outputItems(GFEDishes.CHICKEN_TENDER.get(), 2));

        // ---- c5 烤:烘焙品(隧道式烤炉量产,通用烹饪机亦可小批量) ----
        bake(provider, "bread", 2, 200, 12, b -> b
                .inputItems(GFEFormItems.RAW_BREAD.get(), 2).outputItems(GFEDishes.BREAD.get()));
        bake(provider, "dinner_roll", 2, 200, 12, b -> b
                .inputItems(GFEFormItems.RAW_DINNER_ROLL.get()).outputItems(GFEDishes.DINNER_ROLL.get()));
        bake(provider, "baguette", 2, 240, 12, b -> b
                .inputItems(GFEFormItems.RAW_BAGUETTE.get()).outputItems(GFEDishes.BAGUETTE.get()));
        bake(provider, "toast", 2, 200, 12, b -> b
                .inputItems(GFEFormItems.RAW_TOAST.get()).outputItems(GFEDishes.TOAST.get()));
        bake(provider, "sweet_bread", 2, 240, 12, b -> b
                .inputItems(GFEFormItems.RAW_BREAD.get()).inputItems(Items.SUGAR)
                .outputItems(GFEDishes.SWEET_BREAD.get(), 2));
        bake(provider, "corn_bread", 2, 240, 12, b -> b
                .inputItems(GFECropItems.CORN.get(), 2).inputItems(GFEFormItems.RAW_BREAD.get())
                .outputItems(GFEDishes.CORN_BREAD.get()));
        // 牛肉馅饼(死面菜肴,2026-09-08):面皮包裹,与包子(蒸/猪肉)同构异法
        bake(provider, "beef_pie", 2, 240, 12, b -> b
                .inputItems(GFEFormItems.DOUGH_SHEET.get()).inputItems(GFEFormItems.BEEF_MINCED.get())
                .inputItems(GFEFormItems.ONION_DICED.get())
                .outputItems(GFEDishes.BEEF_PIE.get()));
        bake(provider, "apple_pie", 1, 300, 12, b -> b
                .inputItems(GFEFormItems.DOUGH_SHEET.get()).inputItems(GFEFormItems.APPLE_FLESH.get(), 2)
                .outputItems(GFEDishes.APPLE_PIE.get()));
        bake(provider, "cake", 1, 300, 12, b -> b
                .inputItems(GTItems.DOUGH).inputItems(Items.EGG).inputItems(Items.SUGAR, 2)
                .inputFluids(GTMaterials.Milk, 250)
                .outputItems(GFEDishes.CAKE.get()));
        bake(provider, "baked_corn", 3, 200, 20, b -> b
                .inputItems(GFECropItems.CORN.get(), 2)
                .outputItems(GFEDishes.BAKED_CORN.get(), 2));
        bake(provider, "grilled_fish", 3, 200, 20, b -> b
                .inputItems(GFEFormItems.FISH_CUBE.get())
                .outputItems(GFEDishes.GRILLED_FISH.get()));
        // 蒜香法棍(2026-09-08 暂缓注册):原料蒜末(GARLIC_MINCED)无获取路线(大蒜作物挂 P1),
        // JEI 中显示不可合成配方会误导玩家;大蒜作物落地后恢复本段。
        // bake(provider, "garlic_baguette", 3, 240, 20, b -> b
        //         .inputItems(GFEFormItems.BAGUETTE_SLICE.get())
        //         .inputItems(GFEFormItems.GARLIC_MINCED.get())
        //         .inputFluids(GTMaterials.SeedOil, 20)
        //         .outputItems(GFEDishes.GARLIC_BAGUETTE.get()));

        // ---- c6 烧烤:口味串(生串 + 调味料;200 tick / 16 EU/t,精制档) ----
        cook(provider, "grilled_lamb_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_LAMB_SKEWER.get())
                .outputItems(GFEDishes.GRILLED_LAMB_SKEWER.get()));
        cook(provider, "grilled_beef_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_BEEF_SKEWER.get())
                .outputItems(GFEDishes.GRILLED_BEEF_SKEWER.get()));
        cook(provider, "grilled_chicken_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_CHICKEN_SKEWER.get())
                .outputItems(GFEDishes.GRILLED_CHICKEN_SKEWER.get()));
        cook(provider, "salt_grilled_lamb_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_LAMB_SKEWER.get()).inputItems(GTMaterials.Salt, 1)
                .outputItems(GFEDishes.SALT_GRILLED_LAMB.get()));
        cook(provider, "salt_grilled_beef_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_BEEF_SKEWER.get()).inputItems(GTMaterials.Salt, 1)
                .outputItems(GFEDishes.SALT_GRILLED_BEEF.get()));
        cook(provider, "salt_grilled_chicken_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_CHICKEN_SKEWER.get()).inputItems(GTMaterials.Salt, 1)
                .outputItems(GFEDishes.SALT_GRILLED_CHICKEN.get()));
        cook(provider, "chili_grilled_lamb_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_LAMB_SKEWER.get()).inputItems(GFEFormItems.CHILI_POWDER.get())
                .outputItems(GFEDishes.CHILI_GRILLED_LAMB.get()));
        cook(provider, "chili_grilled_beef_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_BEEF_SKEWER.get()).inputItems(GFEFormItems.CHILI_POWDER.get())
                .outputItems(GFEDishes.CHILI_GRILLED_BEEF.get()));
        cook(provider, "chili_grilled_chicken_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_CHICKEN_SKEWER.get()).inputItems(GFEFormItems.CHILI_POWDER.get())
                .outputItems(GFEDishes.CHILI_GRILLED_CHICKEN.get()));
        // 酱烤(生抽 20 mB 涂刷;2026-09-08 大豆链落地后补)
        cook(provider, "soy_grilled_lamb_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_LAMB_SKEWER.get())
                .inputFluids(GTFEMaterials.SOY_SAUCE, 20)
                .outputItems(GFEDishes.SOY_GRILLED_LAMB.get()));
        cook(provider, "soy_grilled_beef_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_BEEF_SKEWER.get())
                .inputFluids(GTFEMaterials.SOY_SAUCE, 20)
                .outputItems(GFEDishes.SOY_GRILLED_BEEF.get()));
        cook(provider, "soy_grilled_chicken_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_CHICKEN_SKEWER.get())
                .inputFluids(GTFEMaterials.SOY_SAUCE, 20)
                .outputItems(GFEDishes.SOY_GRILLED_CHICKEN.get()));

        // ---- c6 烧烤(铁签串):200 tick / 16 EU/t,精制档 ----
        cook(provider, "iron_lamb_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.FATTY_MUTTON_ROLL.get())
                .inputItems(GFEFormItems.IRON_SKEWER.get())
                .outputItems(GFEDishes.IRON_LAMB_SKEWER.get()));
        cook(provider, "iron_beef_roll_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.FATTY_BEEF_ROLL.get())
                .inputItems(GFEFormItems.IRON_SKEWER.get())
                .outputItems(GFEDishes.IRON_BEEF_ROLL_SKEWER.get()));
        cook(provider, "iron_beef_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.BEEF_SLICE.get(), 2)
                .inputItems(GFEFormItems.IRON_SKEWER.get())
                .outputItems(GFEDishes.IRON_BEEF_SKEWER.get()));

        // ---- c6 烧烤(精制档补全):辣椒串/鸡爪串,与营火版同食材、烤口径 ----
        cook(provider, "grilled_chili_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_CHILI_SKEWER.get())
                .outputItems(GFEDishes.GRILLED_CHILI_SKEWER.get()));
        cook(provider, "grilled_chicken_feet_skewer", 6, 200, 16, b -> b
                .inputItems(GFEFormItems.RAW_CHICKEN_FEET_SKEWER.get())
                .outputItems(GFEDishes.GRILLED_CHICKEN_FEET_SKEWER.get()));

        // ---- 熟牛肉饼(c3 煎):肉饼链生肉饼 → 熟肉饼(dishes-and-gains.md §5) ----
        GFERecipeTypes.COOKING.recipeBuilder(GregFoodExpansion.id("cooking/cooked_beef_patty"))
                .inputItems(GFEFormItems.RAW_BEEF_PATTY.get())
                .inputFluids(FluidIngredient.of(COOKING_OIL, 10))
                .circuitMeta(3)
                .outputItems(GFEFormItems.COOKED_BEEF_PATTY.get())
                .duration(160)
                .EUt(16)
                .save(provider);
    }

    private static void soup(Consumer<FinishedRecipe> provider, String name, int duration, int eut,
                             Consumer<GTRecipeBuilder> config) {
        cook(provider, name, 1, duration, eut, config);
    }

    private static void steam(Consumer<FinishedRecipe> provider, String name, int duration, int eut,
                              Consumer<GTRecipeBuilder> config) {
        cook(provider, name, 2, duration, eut, config);
    }

    private static void stirfry(Consumer<FinishedRecipe> provider, String name, int duration, int eut,
                                Consumer<GTRecipeBuilder> config) {
        cook(provider, name, 3, duration, eut, b -> {
            b.inputFluids(FluidIngredient.of(COOKING_OIL, STIRFRY_OIL));
            config.accept(b);
        });
    }

    // 炸 = 油浴操作:配方装填 1 桶(1,000 mB)食用油,烹饪后回收"装填 − 吸收"。
    // 吸收量(净油耗)不变:裸炸 100 / 挂糊 150 / 重糊 200;回收油经流体输出槽返回可复用。
    private static final int FRY_BATH = 1000;

    private static void fry(Consumer<FinishedRecipe> provider, String name, int duration, int eut,
                            int absorbed, Consumer<GTRecipeBuilder> config) {
        cook(provider, name, 4, duration, eut, b -> {
            b.inputFluids(FluidIngredient.of(COOKING_OIL, FRY_BATH))
                    .outputFluids(GTFEMaterials.USED_COOKING_OIL.getFluid(FRY_BATH - absorbed));
            config.accept(b);
        });
    }

    // c5 烤:面包/吐司/甜面包 = 标准档(2);蛋糕 = 低温档(1);烤玉米/蒜香法棍 = 高温档(3)。
    // 配方一份定义:通用烹饪机小批量可烤,隧道式烤炉并行量产(tunnel-oven.md §6)。
    private static void bake(Consumer<FinishedRecipe> provider, String name, int tier, int duration,
                             int eut, Consumer<GTRecipeBuilder> config) {
        cook(provider, name, 5, duration, eut, b -> {
            b.addData("oven_temp", tier);
            config.accept(b);
        });
    }

    private static void cook(Consumer<FinishedRecipe> provider, String name, int circuit,
                             int duration, int eut, Consumer<GTRecipeBuilder> config) {
        GTRecipeBuilder builder = GFERecipeTypes.COOKING
                .recipeBuilder(GregFoodExpansion.id("cooking/" + name))
                .circuitMeta(circuit)
                .duration(duration)
                .EUt(eut);
        config.accept(builder);
        builder.save(provider);
    }
}
