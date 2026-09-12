package net.mgear.gregfoodexpansion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mgear.gregfoodexpansion.content.runtime.GFContent;

/** Opt-in render smoke, in an isolated directory; never included in release jars. */
@Mod.EventBusSubscriber(modid = "gregfoodexpansion", value = Dist.CLIENT)
public final class ClientSmoke {
    private static boolean done;
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event) {
        if (!Boolean.getBoolean("gregfoodexpansion.clientSmoke") || done || event.phase != TickEvent.Phase.END) return;
        var client = Minecraft.getInstance();
        if (!(client.screen instanceof TitleScreen) && !(client.screen instanceof net.minecraft.client.gui.screens.AccessibilityOnboardingScreen) || client.getOverlay() != null) return;
        done = true;
        try {
            var missing = client.getModelManager().getMissingModel();
            for (var item : GFContent.ITEM_ENTRIES.values()) {
                var model = client.getItemRenderer().getModel(new ItemStack(item.get()), null, null, 0);
                if (model == missing || model.getParticleIcon().contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
                    throw new IllegalStateException("Missing item model/texture: " + item.getId());
                }
            }
            for (var machine : java.util.List.of(net.mgear.gregfoodexpansion.registry.GFMachines.PREP_WORKSHOP,
                    net.mgear.gregfoodexpansion.registry.GFMachines.COOKING_WORKSHOP,
                    net.mgear.gregfoodexpansion.registry.GFMachines.TUNNEL_OVEN)) {
                var model = client.getItemRenderer().getModel(machine.asStack(), null, null, 0);
                if (model == missing || model.getParticleIcon().contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
                    throw new IllegalStateException("Missing machine item model: " + machine.getName());
                }
            }
            for (String key : java.util.List.of("gregfoodexpansion.workshop.requires", "gregfoodexpansion.workshop.missing",
                    "gregfoodexpansion.module.rolling", "gregfoodexpansion.module.boiling",
                    "item.gregfoodexpansion.tomato-egg-noodles-refined")) {
                if (!net.minecraft.client.resources.language.I18n.exists(key)) throw new IllegalStateException("Missing translation: " + key);
            }
            var states = new java.util.ArrayList<net.minecraft.world.level.block.state.BlockState>();
            states.add(GFContent.CLAY_POT.get().defaultBlockState());
            GFContent.CROPS.values().forEach(c -> states.addAll(c.get().getStateDefinition().getPossibleStates()));
            for (var state : states) {
                var model = client.getBlockRenderer().getBlockModel(state);
                if (model == missing || model.getParticleIcon().contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
                    throw new IllegalStateException("Missing block model/texture: " + state);
                }
            }
            GregFoodExpansion.LOGGER.info("GFE_CLIENT_SMOKE_PASSED: {} food/content items, 3 machine items, {} block states",
                    GFContent.ITEM_ENTRIES.size(), states.size());
        } catch (Throwable failure) {
            GregFoodExpansion.LOGGER.error("GFE_CLIENT_SMOKE_FAILED", failure);
        } finally {
            client.stop();
        }
    }
}
