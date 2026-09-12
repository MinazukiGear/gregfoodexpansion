package net.mgear.gregfoodexpansion.content.runtime;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Set;

/** Segment capacity is rebuilt from the matched structure, never from saved inventory or upgrade items. */
public final class WorkshopMachine extends WorkableElectricMultiblockMachine {
    public static final String MODULE_KEY = "gfe_module";
    public static final String SEGMENTS_KEY = "gfe_segments";
    private static final ManagedFieldHolder FIELDS = new ManagedFieldHolder(
            WorkshopMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    private final String module;
    @DescSynced private int segments;

    public WorkshopMachine(IMachineBlockEntity holder, String module) {
        super(holder);
        this.module = module;
    }

    @Override public ManagedFieldHolder getFieldHolder() { return FIELDS; }
    public String module() { return module; }
    public int segments() { return isFormed() ? segments : 0; }

    @Override public void onStructureFormed() {
        Set<BlockPos> positions = getMultiblockState().getMatchContext().getOrDefault(SEGMENTS_KEY, Set.of());
        segments = positions.size();
        super.onStructureFormed();
    }

    @Override public void onStructureInvalid() {
        segments = 0;
        super.onStructureInvalid();
    }

    @Override public void onPartUnload() {
        segments = 0;
        super.onPartUnload();
    }

    public static ModifierFunction modify(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof WorkshopMachine workshop) || !workshop.isFormed()
                || workshop.getEnergyContainer().getHighestInputVoltage() < GTValues.V[GTValues.LV]
                || !workshop.module.equals(recipe.data.getString(MODULE_KEY))
                || workshop.segments() < 1) return ModifierFunction.NULL;
        int parallel = ParallelLogic.getParallelAmount(workshop, recipe, workshop.segments());
        if (parallel < 1) return ModifierFunction.NULL;
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallel))
                .eutMultiplier(parallel).parallels(parallel).build();
    }

    @Override public void addDisplayText(List<Component> lines) {
        super.addDisplayText(lines);
        lines.add(Component.translatable("gregfoodexpansion.workshop.capacity",
                Component.translatable("gregfoodexpansion.module." + module), segments()));
        if (isFormed() && segments() == 0) {
            lines.add(Component.translatable("gregfoodexpansion.workshop.missing",
                    Component.translatable("gregfoodexpansion.module." + module)));
        }
        if (isFormed() && getEnergyContainer().getHighestInputVoltage() < GTValues.V[GTValues.LV]) {
            lines.add(Component.translatable("gregfoodexpansion.workshop.voltage"));
        }
    }
}
