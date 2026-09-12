package net.mgear.gregfoodexpansion.content.runtime;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Crafting wear is returned in the crafting grid; breaking yields no remainder. */
public final class HandToolItem extends Item {
    public HandToolItem() { super(new Properties().durability(128)); }
    @Override public boolean hasCraftingRemainingItem(ItemStack stack) { return true; }
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack result = stack.copy();
        result.setCount(1);
        result.setDamageValue(result.getDamageValue() + 1);
        return result.getDamageValue() >= result.getMaxDamage() ? ItemStack.EMPTY : result;
    }
}
