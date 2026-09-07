package net.mgear.gregfoodexpansion.crop;

import net.minecraft.world.level.block.BushBlock;

/**
 * 野生作物(crop-system-foundation.md §6 补充途径):成熟作物外观的野生植株,
 * 世界生成于温度带匹配的生物群系,破坏即得种子(稳定定向获取,对冲草丛掉落的随机性)。
 * 不生长、无需耕地、不掉产物(种子是独立起点,作物价值在下游加工链)。
 */
public class GFEWildCropBlock extends BushBlock {
    private final int tint;

    public GFEWildCropBlock(Properties properties, int tint) {
        super(properties);
        this.tint = tint;
    }

    /** 与对应作物一致的染色基准色(GFECropBlocks 参数表 / 贴图脚本 TINTS 表)。 */
    public int tint() {
        return tint;
    }
}
