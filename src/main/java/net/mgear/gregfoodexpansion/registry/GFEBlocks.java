package net.mgear.gregfoodexpansion.registry;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 烘焙结构方块(tunnel-oven.md §3,2026-09-08 结构重设计):
 * 隧道体 = 外壳包裹炉膛,炉膛每段为"上火加热管 ×3 / 传送带 ×3 / 下火加热管 ×3",
 * 屋顶每段 2 格烟囱(排气口),两端炉口(进料端嵌控制器)。段数 = 排气口数 ÷ 2。
 */
public final class GFEBlocks {
    private GFEBlocks() {}

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GregFoodExpansion.MOD_ID);

    public static final RegistryObject<Block> TUNNEL_OVEN_CASING = structural("tunnel_oven_casing");
    public static final RegistryObject<Block> TUNNEL_OVEN_BELT = structural("tunnel_oven_belt");
    public static final RegistryObject<Block> TUNNEL_OVEN_HEATER = structural("tunnel_oven_heater");
    public static final RegistryObject<Block> TUNNEL_OVEN_VENT = structural("tunnel_oven_vent");

    public static final List<Pair<String, RegistryObject<Block>>> ALL = List.of(
            Pair.of("tunnel_oven_casing", TUNNEL_OVEN_CASING),
            Pair.of("tunnel_oven_belt", TUNNEL_OVEN_BELT),
            Pair.of("tunnel_oven_heater", TUNNEL_OVEN_HEATER),
            Pair.of("tunnel_oven_vent", TUNNEL_OVEN_VENT));

    private static RegistryObject<Block> structural(String name) {
        return BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL)));
    }
}
