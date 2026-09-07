package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection;
import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

/**
 * food_prep 配方类型(food-processor.md §2/§3,全模组唯一食材处理类型):
 * 物品 ×2 → ×2、无流体(IO 规格[已定案]);第二输出槽留给边角料(概率,后补)。
 * 刀工操作由配方自带编程电路区分:c1 切片 / c2 切丝 / c3 切块 / c4 绞碎 /
 * c5 研磨 / c6 剥皮 / c7 压延 / c8 预留(肉品分割)。
 */
public final class GFERecipeTypes {
    private GFERecipeTypes() {}

    public static GTRecipeType FOOD_PREP;
    /** 通用烹饪类型(universal-cooker.md §2/§4):c1 煮 / c2 蒸 / c3 炒 / c4 炸;中央厨房后续直接复用。 */
    public static GTRecipeType COOKING;

    public static void init(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        ResourceLocation id = GregFoodExpansion.id("food_prep");
        FOOD_PREP = new GTRecipeType(id, GTRecipeTypes.ELECTRIC)
                .setMaxIOSize(2, 2, 0, 0)
                .setEUIO(IO.IN)
                // 数值基准(§5 草案):EU/t 4-8、时长 40-100,基准取中位。
                .prepareBuilder(builder -> builder.duration(60).EUt(4))
                .setSlotOverlay(false, false, GuiTextures.CUTTER_OVERLAY)
                .setProgressBar(GuiTextures.PROGRESS_BAR_SLICE, FillDirection.LEFT_TO_RIGHT)
                .setSound(GTSoundEntries.CUT)
                .setXEIVisible(true);

        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, FOOD_PREP.registryName, FOOD_PREP);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, FOOD_PREP.registryName,
                new GTRecipeSerializer());
        event.register(id, FOOD_PREP);

        ResourceLocation cookingId = GregFoodExpansion.id("cooking");
        COOKING = new GTRecipeType(cookingId, GTRecipeTypes.ELECTRIC)
                .setMaxIOSize(9, 3, 3, 3)
                .setEUIO(IO.IN)
                // 数值基准(universal-cooker.md §5 草案):煮 8-16 / 蒸 12-16 / 炒 16-24 / 炸 24-32。
                .prepareBuilder(builder -> builder.duration(200).EUt(8))
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, FillDirection.LEFT_TO_RIGHT)
                .setSound(GTSoundEntries.FURNACE)
                .setXEIVisible(true);

        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, COOKING.registryName, COOKING);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, COOKING.registryName,
                new GTRecipeSerializer());
        event.register(cookingId, COOKING);
    }
}
