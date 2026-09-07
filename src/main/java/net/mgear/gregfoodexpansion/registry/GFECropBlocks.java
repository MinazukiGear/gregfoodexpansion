package net.mgear.gregfoodexpansion.registry;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.crop.CropEnvSpec;
import net.mgear.gregfoodexpansion.crop.CropFlag;
import net.mgear.gregfoodexpansion.crop.GFECropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 首批 13 作物注册(crop-system-foundation.md §2/§4)。
 * 参数表为 §4 草案基准(实机调参后定案);tint 与生长模板染色基准色
 * (tools/textures/generate_crop_textures.py 的 TINTS 表)必须一致。
 */
public final class GFECropBlocks {
    private GFECropBlocks() {}

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GregFoodExpansion.MOD_ID);

    private static final Map<String, RegistryObject<Block>> BY_CROP = new LinkedHashMap<>();

    public static final RegistryObject<Block> SOYBEAN_CROP = register("soybean",
            new CropEnvSpec(0.6, 1.0, 3, 9, EnumSet.of(CropFlag.NONE)), 0x96BE64);
    public static final RegistryObject<Block> CORN_CROP = register("corn",
            new CropEnvSpec(0.6, 1.0, 3, 9, EnumSet.of(CropFlag.NONE)), 0x7FA84F);
    public static final RegistryObject<Block> RICE_CROP = register("rice",
            new CropEnvSpec(0.7, 1.0, 5, 9, EnumSet.of(CropFlag.NONE)), 0xA8B95C);
    public static final RegistryObject<Block> BARLEY_CROP = register("barley",
            new CropEnvSpec(0.4, 0.9, 2, 9, EnumSet.of(CropFlag.NONE)), 0xBCAE64);
    public static final RegistryObject<Block> PEANUT_CROP = register("peanut",
            new CropEnvSpec(0.7, 1.0, 2, 9, EnumSet.of(CropFlag.NONE)), 0x7FA354);
    public static final RegistryObject<Block> TOMATO_CROP = register("tomato",
            new CropEnvSpec(0.6, 1.0, 3, 9, EnumSet.of(CropFlag.NONE)), 0x74A854);
    public static final RegistryObject<Block> ONION_CROP = register("onion",
            new CropEnvSpec(0.4, 0.9, 2, 9, EnumSet.of(CropFlag.NONE)), 0x74A08C);
    public static final RegistryObject<Block> CHILI_CROP = register("chili",
            new CropEnvSpec(0.7, 1.2, 2, 9, EnumSet.of(CropFlag.NONE)), 0x64A054);
    public static final RegistryObject<Block> CABBAGE_CROP = register("cabbage",
            new CropEnvSpec(0.3, 0.8, 3, 9, EnumSet.of(CropFlag.NONE)), 0x90C06E);
    public static final RegistryObject<Block> GRAPE_CROP = register("grape",
            new CropEnvSpec(0.5, 0.9, 3, 9, EnumSet.of(CropFlag.NONE)), 0x74A05C);
    public static final RegistryObject<Block> COFFEE_CROP = register("coffee",
            new CropEnvSpec(0.9, 1.2, 4, 9, EnumSet.of(CropFlag.NONE)), 0x55783E);
    public static final RegistryObject<Block> TEA_CROP = register("tea",
            new CropEnvSpec(0.6, 1.0, 5, 9, EnumSet.of(CropFlag.NONE)), 0x44703C);
    public static final RegistryObject<Block> HOPS_CROP = register("hops",
            new CropEnvSpec(0.4, 0.9, 3, 9, EnumSet.of(CropFlag.NONE)), 0x90C46A);

    public static final List<RegistryObject<Block>> ALL_CROPS = List.of(
            SOYBEAN_CROP, CORN_CROP, RICE_CROP, BARLEY_CROP, PEANUT_CROP, TOMATO_CROP,
            ONION_CROP, CHILI_CROP, CABBAGE_CROP, GRAPE_CROP, COFFEE_CROP, TEA_CROP, HOPS_CROP);

    public static RegistryObject<Block> blockOf(String crop) {
        return BY_CROP.get(crop);
    }

    private static RegistryObject<Block> register(String crop, CropEnvSpec spec, int tint) {
        RegistryObject<Block> block = BLOCKS.register(crop + "_crop",
                () -> new GFECropBlock(cropProperties(), spec, tint, GFECropItems.seedOf(crop)));
        BY_CROP.put(crop, block);
        return block;
    }

    // vanilla Blocks.WHEAT 的方块属性:无碰撞、随机刻、即碎、作物音效、活塞推毁。
    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY);
    }
}
