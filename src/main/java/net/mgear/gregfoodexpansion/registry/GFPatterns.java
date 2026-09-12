package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * MV 隧道烤炉结构(拟真修订 2026-09-13):7×3×3 隧道机架。
 *
 * <p>层约定沿用姊妹项目实证语法:层自下而上、层内行自后向前、字符自西向东。
 * 长向 = 物流方向:中段气道贯通(5 格)、驱动端不锈钢齿轮箱(V,West 端)、
 * 顶面钢管烟道排(U,对应真实隧道炉的排烟管列)。炉温段(低/中/高温)沿长向
 * 以结构段扩展,随配方批次追加(machines.md §5.3)。</p>
 */
public final class GFPatterns {
    /** 驱动端固定件:不锈钢齿轮箱(驱动辊组)。 */
    private static final char DRIVE = 'V';
    /** 顶面烟道固定件:钢管外壳。 */
    private static final char FLUE = 'U';

    private static final String[][] TUNNEL_LAYERS = {
            {"CCCCCCC", DRIVE + "CCCCCC", "CCCCCCC"},
            {"CCCECCC", "CAAAAAC", "CICDOCC"},
            {"CCCCCCC", "C" + FLUE + FLUE + FLUE + FLUE + "C", "CCCCCCC"},
    };

    private static Map<Character, Block> fixtures() {
        return Map.of(DRIVE, GTBlocks.CASING_STAINLESS_STEEL_GEARBOX.get(),
                FLUE, GTBlocks.CASING_STEEL_PIPE.get());
    }

    public static BlockPattern tunnelOven(MultiblockMachineDefinition definition, Block casing) {
        return boxPattern(TUNNEL_LAYERS, casing, definition, fixtures());
    }

    private static BlockPattern boxPattern(String[][] layers, Block casing,
                                           MultiblockMachineDefinition definition,
                                           Map<Character, Block> fixtures) {
        // 预览符号在运行时归一为外壳候选:任意仓室可砌入任意外壳位
        Map<Character, Character> aliases = Map.of('I', 'C', 'O', 'C', 'E', 'C');
        FactoryBlockPattern builder = FactoryBlockPattern.start(
                RelativeDirection.LEFT, RelativeDirection.FRONT, RelativeDirection.UP);
        for (String[] layer : layers) {
            builder.aisle(applyAliases(layer, aliases));
        }
        TraceabilityPredicate hatchable = Predicates.blocks(casing)
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS))
                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS))
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY));
        for (char symbol : symbols(layers)) {
            switch (symbol) {
                case 'C', 'I', 'O', 'E' -> builder.where(symbol, hatchable);
                case 'A' -> builder.where(symbol, Predicates.air());
                case 'D' -> builder.where(symbol,
                        Predicates.controller(Predicates.blocks(definition.getBlock())));
                default -> builder.where(symbol, Predicates.blocks(fixtures.get(symbol)));
            }
        }
        return builder.build();
    }

    public static MultiblockShapeInfo tunnelOvenShape(MultiblockMachineDefinition definition, Block casing) {
        return boxShape(TUNNEL_LAYERS, casing, definition, GTValues.MV, fixtures());
    }

    private static MultiblockShapeInfo boxShape(String[][] layers, Block casing,
                                                MultiblockMachineDefinition definition, int hatchTier,
                                                Map<Character, Block> fixtures) {
        // 层约定 → 预览坐标系换算与姊妹项目一致:自前向后逐行,行内自下而上取各层同位字符。
        int height = layers.length;
        int width = layers[0][0].length();
        int depth = layers[0].length;
        MultiblockShapeInfo.ShapeInfoBuilder builder = MultiblockShapeInfo.builder();
        for (int row = depth - 1; row >= 0; row--) {
            String[] previewRows = new String[height];
            for (int layer = 0; layer < height; layer++) {
                StringBuilder line = new StringBuilder(width);
                for (int column = 0; column < width; column++) {
                    line.append(layers[layer][row].charAt(column));
                }
                previewRows[layer] = line.toString();
            }
            builder.aisle(previewRows);
        }
        builder.where('C', casing);
        builder.where('I', GTMachines.ITEM_IMPORT_BUS[hatchTier], Direction.NORTH);
        builder.where('O', GTMachines.ITEM_EXPORT_BUS[hatchTier], Direction.NORTH);
        builder.where('E', GTMachines.ENERGY_INPUT_HATCH[hatchTier], Direction.NORTH);
        builder.where('A', Blocks.AIR);
        builder.where('D', definition, Direction.NORTH);
        fixtures.forEach(builder::where);
        return builder.build();
    }

    private static String[] applyAliases(String[] layer, Map<Character, Character> aliases) {
        String[] out = new String[layer.length];
        for (int i = 0; i < layer.length; i++) {
            StringBuilder line = new StringBuilder(layer[i].length());
            for (int j = 0; j < layer[i].length(); j++) {
                line.append(aliases.getOrDefault(layer[i].charAt(j), layer[i].charAt(j)));
            }
            out[i] = line.toString();
        }
        return out;
    }

    private static SortedSet<Character> symbols(String[][] layers) {
        SortedSet<Character> set = new TreeSet<>();
        for (String[] layer : layers) {
            for (String row : layer) {
                for (int i = 0; i < row.length(); i++) {
                    set.add(row.charAt(i));
                }
            }
        }
        return set;
    }

    private GFPatterns() {}
}
