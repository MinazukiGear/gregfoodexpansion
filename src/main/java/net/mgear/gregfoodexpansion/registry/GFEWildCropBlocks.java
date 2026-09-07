package net.mgear.gregfoodexpansion.registry;

import java.util.List;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.crop.GFEWildCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 野生作物方块注册(crop-system-foundation.md §6 补充途径)。
 * ALL 列表顺序与 GFECropBlocks.ALL_CROPS / GFECropItems 列表一致(§4.1 首批清单序),
 * 便于掉落表与染色的按下标配对;tint 必须与对应作物一致(同一张 stage_3 模板染色)。
 * 世界生成分布:温和带生物群系标签 data/gregfoodexpansion/tags/worldgen/biome/*.json
 * + 各作物的 random_patch 特征与 forge:add_features 生物群系修饰器(纯数据)。
 */
public final class GFEWildCropBlocks {
    private GFEWildCropBlocks() {}

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GregFoodExpansion.MOD_ID);

    public static final RegistryObject<Block> WILD_SOYBEAN = register("soybean", 0x96BE64);
    public static final RegistryObject<Block> WILD_CORN = register("corn", 0x7FA84F);
    public static final RegistryObject<Block> WILD_RICE = register("rice", 0xA8B95C);
    public static final RegistryObject<Block> WILD_BARLEY = register("barley", 0xBCAE64);
    public static final RegistryObject<Block> WILD_PEANUT = register("peanut", 0x7FA354);
    public static final RegistryObject<Block> WILD_TOMATO = register("tomato", 0x74A854);
    public static final RegistryObject<Block> WILD_ONION = register("onion", 0x74A08C);
    public static final RegistryObject<Block> WILD_CHILI = register("chili", 0x64A054);
    public static final RegistryObject<Block> WILD_CABBAGE = register("cabbage", 0x90C06E);
    public static final RegistryObject<Block> WILD_GRAPE = register("grape", 0x74A05C);
    public static final RegistryObject<Block> WILD_COFFEE = register("coffee", 0x55783E);
    public static final RegistryObject<Block> WILD_TEA = register("tea", 0x44703C);
    public static final RegistryObject<Block> WILD_HOPS = register("hops", 0x90C46A);

    public static final List<RegistryObject<Block>> ALL = List.of(
            WILD_SOYBEAN, WILD_CORN, WILD_RICE, WILD_BARLEY, WILD_PEANUT, WILD_TOMATO,
            WILD_ONION, WILD_CHILI, WILD_CABBAGE, WILD_GRAPE, WILD_COFFEE, WILD_TEA, WILD_HOPS);

    private static RegistryObject<Block> register(String crop, int tint) {
        return BLOCKS.register("wild_" + crop, () -> new GFEWildCropBlock(properties(), tint));
    }

    // vanilla 花的属性:无碰撞、即碎、草音色;不 randomTicks(野生植株不生长)。
    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY);
    }
}
