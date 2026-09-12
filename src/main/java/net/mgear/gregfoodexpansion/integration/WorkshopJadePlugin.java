package net.mgear.gregfoodexpansion.integration;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.content.runtime.WorkshopMachine;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Optional Jade entry point; no common class loads Jade APIs when Jade is absent. */
@WailaPlugin
public final class WorkshopJadePlugin implements IWailaPlugin {
    private static final Provider PROVIDER = new Provider();

    @Override public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(PROVIDER, BlockEntity.class);
    }
    @Override public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(PROVIDER, Block.class);
    }

    public static final class Provider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        @Override public ResourceLocation getUid() { return GregFoodExpansion.id("workshop_segments"); }
        @Override public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            if (MetaMachine.getMachine(accessor.getLevel(), accessor.getPosition()) instanceof WorkshopMachine machine) {
                var data = new CompoundTag();
                data.putString("module", machine.module());
                data.putInt("segments", machine.segments());
                data.putBoolean("formed", machine.isFormed());
                tag.put("gfeWorkshop", data);
            }
        }
        @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!accessor.getServerData().contains("gfeWorkshop")) return;
            var data = accessor.getServerData().getCompound("gfeWorkshop");
            var module = Component.translatable("gregfoodexpansion.module." + data.getString("module"));
            tooltip.add(Component.translatable("gregfoodexpansion.workshop.capacity", module, data.getInt("segments")));
            if (data.getBoolean("formed") && data.getInt("segments") == 0) {
                tooltip.add(Component.translatable("gregfoodexpansion.workshop.missing", module));
            }
        }
    }
}
