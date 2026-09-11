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
 * 多方块结构图案(骨架首版:基础机架,结构化模块段随内容批次追加,machines.md §5.0)。
 *
 * <p>层约定沿用姊妹项目实证语法:层自下而上、层内行自后向前、字符自西向东;
 * 预览符号(输入总线/输出总线/能源仓)在运行时图案中归一为"外壳或仓室"候选谓词,
 * 使任意仓室可砌入任意外壳位(最小结构 = 全外壳 + 控制器,仓室可缺省空转)。</p>
 */
public final class GFPatterns {
    /** 3×3×3 厨房工坊级(切配工坊/烹饪工坊共用骨架)。预览中仓室砌于正面与底面。 */
    private static final String[][] WORKSHOP_LAYERS = {
            {"CCC", "CEC", "CCC"},
            {"CCC", "CCC", "IDO"},
            {"CCC", "CCC", "CCC"},
    };

    /** 5×3×3 隧道机架级(隧道烤炉:长度方向 = 物流方向,炉温段沿长向扩展)。 */
    private static final String[][] TUNNEL_LAYERS = {
            {"CCCCC", "CCCCC", "CCCCC"},
            {"CACAC", "CA#AC", "CIDOC"},
            {"CCCCC", "CCCCC", "CCCCC"},
    };

    public static BlockPattern prepWorkshop(MultiblockMachineDefinition definition, Block casing) {
        return boxPattern(WORKSHOP_LAYERS, casing, definition);
    }

    public static BlockPattern cookingWorkshop(MultiblockMachineDefinition definition, Block casing) {
        return boxPattern(WORKSHOP_LAYERS, casing, definition);
    }

    public static BlockPattern tunnelOven(MultiblockMachineDefinition definition, Block casing) {
        return boxPattern(TUNNEL_LAYERS, casing, definition);
    }

    private static BlockPattern boxPattern(String[][] layers, Block casing,
                                           MultiblockMachineDefinition definition) {
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
                case 'A', '#' -> builder.where(symbol, Predicates.air());
                case 'D' -> builder.where(symbol,
                        Predicates.controller(Predicates.blocks(definition.getBlock())));
                default -> throw new IllegalArgumentException("未知结构符号: " + symbol);
            }
        }
        return builder.build();
    }

    /** 预览布局(EMI/Jade 结构预览):仓室以实际机器渲染。 */
    public static MultiblockShapeInfo workshopShape(MultiblockMachineDefinition definition, Block casing) {
        return boxShape(WORKSHOP_LAYERS, casing, definition);
    }

    public static MultiblockShapeInfo tunnelOvenShape(MultiblockMachineDefinition definition, Block casing) {
        return boxShape(TUNNEL_LAYERS, casing, definition);
    }

    private static MultiblockShapeInfo boxShape(String[][] layers, Block casing,
                                                MultiblockMachineDefinition definition) {
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
        builder.where('I', GTMachines.ITEM_IMPORT_BUS[GTValues.LV], Direction.NORTH);
        builder.where('O', GTMachines.ITEM_EXPORT_BUS[GTValues.LV], Direction.NORTH);
        builder.where('E', GTMachines.ENERGY_INPUT_HATCH[GTValues.LV], Direction.NORTH);
        builder.where('A', Blocks.AIR);
        builder.where('#', Blocks.AIR);
        builder.where('D', definition, Direction.NORTH);
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
