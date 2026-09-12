package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.*;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.mgear.gregfoodexpansion.content.runtime.WorkshopMachine;
import java.util.HashSet;

/** Front, service cavity, zero to four complete processing slices, rear cap. */
public final class WorkshopPatterns {
    public static final int MAX_SEGMENTS = 4;
    private WorkshopPatterns() {}

    public static Block moduleBlock(boolean cooking) {
        return cooking ? GTBlocks.CASING_BRONZE_PIPE.get() : GTBlocks.CASING_STEEL_GEARBOX.get();
    }

    public static BlockPattern pattern(MultiblockMachineDefinition definition, boolean cooking) {
        Block steel = GTBlocks.CASING_STEEL_SOLID.get();
        Block module = moduleBlock(cooking);
        var hatchable = Predicates.blocks(steel)
                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(cooking ? 2 : 1))
                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1))
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(2))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(cooking ? 1 : 0));
        var segment = Predicates.custom(state -> {
            if (!state.getBlockState().is(module)) return false;
            state.getMatchContext().getOrCreate(WorkshopMachine.SEGMENTS_KEY, HashSet::new)
                    .add(state.getPos().immutable());
            return true;
        }, () -> new BlockInfo[]{BlockInfo.fromBlockState(module.defaultBlockState())})
                .addTooltips(Component.translatable("gregfoodexpansion.module." + (cooking ? "boiling" : "rolling")));
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .aisle("CCC", "CDC", "CCC")
                .aisle("CCC", "C#C", "CCC")
                .aisle("SSS", "SMS", "SSS").setRepeatable(0, MAX_SEGMENTS)
                .aisle("SSS", "SSS", "SSS")
                .where('C', hatchable)
                .where('S', Predicates.blocks(steel))
                .where('M', segment)
                .where('#', Predicates.air())
                .where('D', Predicates.controller(Predicates.blocks(definition.getBlock())))
                .build();
    }

    /** The same front-to-back convention is used in the pattern, preview and server tests. */
    public static MultiblockShapeInfo shape(MultiblockMachineDefinition definition, boolean cooking, int count) {
        if (count < 0 || count > MAX_SEGMENTS) throw new IllegalArgumentException("Invalid segment count");
        var builder = MultiblockShapeInfo.builder()
                .aisle("CCC", "IDO", "CCC")
                .aisle("CEC", "I#O", cooking ? "CFC" : "CCC");
        for (int i = 0; i < count; i++) builder.aisle("CCC", "CMC", "CCC");
        return builder.aisle("CCC", "CCC", "CCC")
                .where('C', GTBlocks.CASING_STEEL_SOLID.get())
                .where('M', moduleBlock(cooking))
                .where('#', net.minecraft.world.level.block.Blocks.AIR)
                .where('I', GTMachines.ITEM_IMPORT_BUS[GTValues.LV], Direction.WEST)
                .where('O', GTMachines.ITEM_EXPORT_BUS[GTValues.LV], Direction.EAST)
                .where('E', GTMachines.ENERGY_INPUT_HATCH[GTValues.LV], Direction.DOWN)
                .where('F', GTMachines.FLUID_IMPORT_HATCH[GTValues.LV], Direction.UP)
                .where('D', definition, Direction.NORTH).build();
    }
}
