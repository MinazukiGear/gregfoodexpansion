package net.mgear.gregfoodexpansion.registry;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.loot.CropSeedDropModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Global Loot Modifier 注册(Forge 1.20.1:注册表直接持有 Codec)。
 * modifier 实例经 data/gregfoodexpansion/loot_modifiers/*.json + forge 的
 * global_loot_modifiers.json 选择器挂载。
 */
public final class GFECropLootModifiers {
    private GFECropLootModifiers() {}

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
                    GregFoodExpansion.MOD_ID);

    public static final Supplier<Codec<? extends IGlobalLootModifier>> CROP_SEEDS =
            SERIALIZERS.register("crop_seeds", () -> CropSeedDropModifier.CODEC);
}
