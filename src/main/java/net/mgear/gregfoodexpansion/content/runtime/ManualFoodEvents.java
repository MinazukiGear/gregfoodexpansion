package net.mgear.gregfoodexpansion.content.runtime;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** The manual water bridge remains an ordinary item, with no GT fluid capability. */
@Mod.EventBusSubscriber(modid = "gregfoodexpansion")
public final class ManualFoodEvents {
    @SubscribeEvent
    public static void fillBowl(PlayerInteractEvent.RightClickItem event) {
        if (!event.getItemStack().is(Items.BOWL)) return;
        var player = event.getEntity();
        var level = event.getLevel();
        var start = player.getEyePosition();
        var reach = player.getAttributeValue(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get());
        var hit = level.clip(new ClipContext(start, start.add(player.getLookAngle().scale(reach)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
        if (hit.getType() != HitResult.Type.BLOCK) return;
        var pos = ((BlockHitResult) hit).getBlockPos();
        if (!level.getFluidState(pos).is(Fluids.WATER) || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, hit.getDirection(), event.getItemStack())) return;
        if (!level.isClientSide) {
            player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(event.getItemStack(), player,
                    new ItemStack(GFContent.item("water_bowl"))));
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
