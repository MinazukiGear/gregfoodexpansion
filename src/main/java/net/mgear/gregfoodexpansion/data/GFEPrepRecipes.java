package net.mgear.gregfoodexpansion.data;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.common.data.GTItems;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.prep.GFEFormItems;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.mgear.gregfoodexpansion.registry.GFERecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

/**
 * 切配机电路配方(food-processor.md §3/§4/§5 草案数值,基准):
 * c1 切片 / c2 切丝 / c3 切块 / c4 绞碎 / c5 研磨 / c6 剥皮 / c7 压延;
 * c8 原为预留,实现期提案分配给"带骨切件"(排骨),随 M2 肉品线专项正式化。
 * 生熟维度判定(dishes-and-gains.md §4):鸡肉丝仅熟鸡(c2),鸡块取熟鸡(c3),
 * 其余鸡肉形态取生鸡。经 IGTAddon#addRecipes 走 GTCEu 动态数据包注册。
 */
public final class GFEPrepRecipes {
    private GFEPrepRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        // ---- c1 切片:40 tick / 4 EU/t ----
        prep(provider, 1, 40, 4, Items.BEEF, GFEFormItems.BEEF_SLICE.get());
        prep(provider, 1, 40, 4, Items.PORKCHOP, GFEFormItems.PORK_SLICE.get());
        prep(provider, 1, 40, 4, Items.MUTTON, GFEFormItems.MUTTON_SLICE.get());
        prep(provider, 1, 40, 4, Items.CHICKEN, GFEFormItems.CHICKEN_SLICE.get());
        prep(provider, 1, 40, 4, Items.COD, GFEFormItems.FISH_SLICE.get());
        prep(provider, 1, 40, 4, Items.SALMON, GFEFormItems.FISH_SLICE.get());
        prep(provider, 1, 40, 4, Items.POTATO, GFEFormItems.POTATO_SLICE.get());
        prep(provider, 1, 40, 4, Items.APPLE, GFEFormItems.APPLE_SLICE.get());
        prep(provider, 1, 40, 4, GFECropItems.TOMATO.get(), GFEFormItems.TOMATO_SLICE.get());
        prep(provider, 1, 40, 4, GFECropItems.CHILI.get(), GFEFormItems.CHILI_RING.get());

        // ---- c2 切丝:40 tick / 4 EU/t ----
        prep(provider, 2, 40, 4, Items.BEEF, GFEFormItems.BEEF_STRIP.get());
        prep(provider, 2, 40, 4, Items.PORKCHOP, GFEFormItems.PORK_STRIP.get());
        prep(provider, 2, 40, 4, Items.POTATO, GFEFormItems.POTATO_STRIP.get());
        prep(provider, 2, 40, 4, Items.CARROT, GFEFormItems.CARROT_STRIP.get());
        prep(provider, 2, 40, 4, GFECropItems.CABBAGE.get(), GFEFormItems.CABBAGE_STRIP.get());
        prep(provider, 2, 40, 4, GFECropItems.CHILI.get(), GFEFormItems.CHILI_STRIP.get());
        prep(provider, 2, 40, 4, Items.COOKED_CHICKEN, GFEFormItems.CHICKEN_SHRED.get());

        // ---- c3 切块:40 tick / 4 EU/t ----
        prep(provider, 3, 40, 4, Items.BEEF, GFEFormItems.BEEF_CUBE.get());
        prep(provider, 3, 40, 4, Items.PORKCHOP, GFEFormItems.PORK_CUBE.get());
        prep(provider, 3, 40, 4, Items.MUTTON, GFEFormItems.MUTTON_CUBE.get());
        prep(provider, 3, 40, 4, Items.CHICKEN, GFEFormItems.CHICKEN_DICED.get());
        prep(provider, 3, 40, 4, Items.COOKED_CHICKEN, GFEFormItems.CHICKEN_CUTS.get());
        prep(provider, 3, 40, 4, Items.COD, GFEFormItems.FISH_CUBE.get());
        prep(provider, 3, 40, 4, Items.SALMON, GFEFormItems.FISH_CUBE.get());
        prep(provider, 3, 40, 4, GFECropItems.TOMATO.get(), GFEFormItems.TOMATO_DICED.get());
        prep(provider, 3, 40, 4, GFECropItems.ONION.get(), GFEFormItems.ONION_DICED.get());
        prep(provider, 3, 40, 4, Items.POTATO, GFEFormItems.FRIES_BLANK.get());
        prep(provider, 3, 40, 4, GFECropItems.CHILI.get(), GFEFormItems.CHILI_DICED.get());

        // ---- c4 绞碎:60 tick / 4 EU/t ----
        prep(provider, 4, 60, 4, Items.BEEF, GFEFormItems.BEEF_MINCED.get());
        prep(provider, 4, 60, 4, Items.PORKCHOP, GFEFormItems.PORK_MINCED.get());
        prep(provider, 4, 60, 4, Items.MUTTON, GFEFormItems.MUTTON_MINCED.get());
        prep(provider, 4, 60, 4, Items.CHICKEN, GFEFormItems.CHICKEN_MINCED.get());
        prep(provider, 4, 60, 4, Items.COD, GFEFormItems.FISH_SURIMI.get());
        prep(provider, 4, 60, 4, Items.SALMON, GFEFormItems.FISH_SURIMI.get());
        prep(provider, 4, 60, 4, GFECropItems.ONION.get(), GFEFormItems.ONION_MINCED.get());

        // ---- c5 研磨:80 tick / 6 EU/t ----
        prep(provider, 5, 80, 6, GFECropItems.CHILI.get(), GFEFormItems.CHILI_POWDER.get());

        // ---- c6 剥皮:40 tick / 4 EU/t ----
        prep(provider, 6, 40, 4, Items.APPLE, GFEFormItems.APPLE_FLESH.get());

        // ---- c8 带骨切件(实现期提案):40 tick / 4 EU/t ----
        prep(provider, 8, 40, 4, Items.BEEF, GFEFormItems.BEEF_RIBS.get());
        prep(provider, 8, 40, 4, Items.PORKCHOP, GFEFormItems.PORK_RIBS.get());

        // ---- c7 压延:100 tick / 8 EU/t(§5 压延取上限),原料 = GTCEu 面团 ----
        prep(provider, 7, 100, 8, GTItems.DOUGH.get(), GFEFormItems.NOODLE.get());
        prep(provider, 7, 100, 8, GTItems.DOUGH.get(), GFEFormItems.DOUGH_SHEET.get());
    }

    private static void prep(Consumer<FinishedRecipe> provider, int circuit, int duration,
                             int eut, ItemLike input, ItemLike output) {
        String in = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(input.asItem()).getPath();
        String out = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(output.asItem()).getPath();
        GFERecipeTypes.FOOD_PREP.recipeBuilder(GregFoodExpansion.id("food_prep/" + in + "_to_" + out))
                .inputItems(input)
                .circuitMeta(circuit)
                .outputItems(output)
                .duration(duration)
                .EUt(eut)
                .save(provider);
    }
}
