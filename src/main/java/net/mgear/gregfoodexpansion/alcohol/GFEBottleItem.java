package net.mgear.gregfoodexpansion.alcohol;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;

/**
 * 瓶装酒(alcohol-line.md §5):Drinkable 食品,喝完还玻璃瓶。
 * 饮用链路与 GFEFoodItem 一致(进食效果走 §2.1 增益管线),
 * 额外覆写:饮用音效(GENERIC_DRINK,非咀嚼)与空瓶返还(原版 HoneyBottle 同款语义)。
 */
public class GFEBottleItem extends net.mgear.gregfoodexpansion.gains.GFEFoodItem {
    public GFEBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (result.isEmpty()) {
            return new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE);
        }
        // 创造模式物品不缩减(super 已处理非创造):此处仅补还瓶
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack bottle = new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
        return result;
    }

    @Override
    public net.minecraft.sounds.SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public net.minecraft.sounds.SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }
}
