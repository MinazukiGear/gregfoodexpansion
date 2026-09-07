package net.mgear.gregfoodexpansion.baking;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.registry.GFEBlocks;
import net.mgear.gregfoodexpansion.registry.GFERecipeTypes;

/**
 * 隧道式烤炉结构(tunnel-oven.md §3,2026-09-08 重设计:接近真实烤炉观感):
 *
 * <pre>
 * 进料端(炉口)          隧道段 ×2-8            出料端
 * █████████          ██烟囱██          █████████
 * █████████          ████火火火██          █████████
 * ███控制器██          ██带带带██          ██░░░██(出料口)
 * ██火火火██(发光炉膛)    ████火火火██          █████████
 * </pre>
 *
 * 截面 5 宽 × 5 高(含烟囱),长度 = 炉口 + 2-8 段 + 出料端;段数由排气口计数
 * (每段 2 格)写入 match context,并行数 = 段数 × 2。
 */
public final class GFEOvenPatterns {
    private GFEOvenPatterns() {}

    public static BlockPattern createPattern(MultiblockMachineDefinition definition) {
        return FactoryBlockPattern.start()
                .aisle("CCCCC", "CCCCC", "CCKCC", "CCCCC")
                .aisleRepeatable(2, 8, "CCVCC", "CHHHC", "CTTTC", "CCHCC")
                .aisle("CCCCC", "CCCCC", "CCC#C", "CHHHC")
                .where('K', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .where('V', vent())
                .where('H', Predicates.blocks(GFEBlocks.TUNNEL_OVEN_HEATER.get()))
                .where('T', Predicates.blocks(GFEBlocks.TUNNEL_OVEN_BELT.get()))
                .where('#', Predicates.air())
                .where('C', Predicates.blocks(GFEBlocks.TUNNEL_OVEN_CASING.get())
                        .or(Predicates.autoAbilities(GFERecipeTypes.COOKING)))
                .build();
    }

    /** 排气口计数写入 match context("VentCount"),供段数与并行数计算。 */
    private static TraceabilityPredicate vent() {
        return new TraceabilityPredicate(new SimplePredicate(state -> {
            if (state.getBlockState().is(GFEBlocks.TUNNEL_OVEN_VENT.get())) {
                state.getMatchContext().increment("VentCount", 1);
                return true;
            }
            return false;
        }, () -> null));
    }
}
