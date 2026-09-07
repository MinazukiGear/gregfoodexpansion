package net.mgear.gregfoodexpansion.prep;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 手工工具的耐久消耗机制:Forge 在合成完成后按格取剩余物,
 * 这里返回损伤 +1 的自身;耐久耗尽后不再返还(工具消耗)。
 */
public class GFEPrepToolItem extends Item {

    public GFEPrepToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return ItemStack.EMPTY;
        }
        ItemStack damaged = stack.copy();
        damaged.setDamageValue(stack.getDamageValue() + 1);
        return damaged;
    }
}
