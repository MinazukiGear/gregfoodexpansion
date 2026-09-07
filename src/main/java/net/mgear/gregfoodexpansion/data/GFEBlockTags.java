package net.mgear.gregfoodexpansion.data;

import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.mgear.gregfoodexpansion.registry.GFECropBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public final class GFEBlockTags {
    private GFEBlockTags() {}

    public static void init(RegistrateTagsProvider<Block> provider) {
        // minecraft:crops(蜂群/农场判定等通用口径)+ mineable/hoe(锹速采集,同 vanilla 小麦)。
        GFECropBlocks.ALL_CROPS.forEach(crop -> {
            provider.addTag(BlockTags.CROPS).add(key(provider, crop.get()));
            provider.addTag(BlockTags.MINEABLE_WITH_HOE).add(key(provider, crop.get()));
        });
    }

    // Registrate 非 intrinsic 的 TagAppender 只接受 ResourceKey。
    private static net.minecraft.resources.ResourceKey<Block> key(RegistrateTagsProvider<Block> provider, Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
    }
}
