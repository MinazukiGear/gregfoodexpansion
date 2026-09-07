package net.mgear.gregfoodexpansion.client;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.crop.GFECropBlock;
import net.mgear.gregfoodexpansion.registry.GFECropBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 灰度生长模板按作物基准色染色(颜色源:GFECropBlock#tint,与贴图脚本 TINTS 表一致)。 */
@Mod.EventBusSubscriber(modid = GregFoodExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)
public final class GFECropColors {
    private GFECropColors() {}

    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        for (var crop : GFECropBlocks.ALL_CROPS) {
            GFECropBlock block = (GFECropBlock) crop.get();
            event.register((state, level, pos, tintIndex) -> block.tint(), block);
        }
    }
}
