package net.mgear.gregfoodexpansion.content.runtime;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Server-authoritative cooking. Unloaded chunks pause; progress and servings survive reloads. */
public final class ClayPotBlockEntity extends BlockEntity {
    private final SimpleContainer inputs = new SimpleContainer(4);
    private int water;
    private int progress;
    private String activeRecipe = "";
    private ItemStack meal = ItemStack.EMPTY;
    private int servings;

    public ClayPotBlockEntity(BlockPos pos, BlockState state) { super(GFContent.POT_ENTITY.get(), pos, state); }
    public SimpleContainer inputs() { return inputs; }
    public int water() { return water; }
    public int progress() { return progress; }
    public int servings() { return servings; }

    public void interact(Player player, InteractionHand hand) {
        var held = player.getItemInHand(hand);
        if (held.is(Items.BOWL) && servings > 0) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, meal.copy()));
            servings--;
            if (servings == 0) meal = ItemStack.EMPTY;
        } else if (held.is(GFContent.item("water_bowl")) && water < 4 && servings == 0) {
            water++;
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, new ItemStack(Items.BOWL)));
        } else if (held.isEmpty() && player.isShiftKeyDown() && servings == 0) {
            for (int slot = 3; slot >= 0; slot--) {
                if (!inputs.getItem(slot).isEmpty()) {
                    player.setItemInHand(hand, inputs.removeItemNoUpdate(slot));
                    resetProgress();
                    break;
                }
            }
        } else if (!held.isEmpty() && !held.is(Items.BOWL) && !held.is(GFContent.item("water_bowl")) && servings == 0) {
            for (int slot = 0; slot < 4; slot++) {
                if (inputs.getItem(slot).isEmpty()) {
                    inputs.setItem(slot, held.copyWithCount(1));
                    if (!player.getAbilities().instabuild) held.shrink(1);
                    resetProgress();
                    break;
                }
            }
        }
        setChanged();
        showStatus(player);
    }

    private void resetProgress() { progress = 0; activeRecipe = ""; }

    private void showStatus(Player player) {
        if (servings > 0) {
            player.displayClientMessage(Component.translatable("gregfoodexpansion.pot.ready", meal.getHoverName(), servings), true);
            return;
        }
        var contents = Component.empty();
        for (int i = 0; i < 4; i++) {
            if (i > 0) contents.append(" / ");
            contents.append(inputs.getItem(i).isEmpty() ? Component.literal("-") : inputs.getItem(i).getHoverName());
        }
        player.displayClientMessage(Component.translatable("gregfoodexpansion.pot.status", contents, water, progress / 20), true);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ClayPotBlockEntity pot) {
        if (pot.servings > 0) return;
        var recipe = level.getRecipeManager().getRecipeFor(GFContent.POT_RECIPE_TYPE.get(), pot.inputs, level).orElse(null);
        if (recipe == null) {
            if (pot.progress != 0 || !pot.activeRecipe.isEmpty()) { pot.resetProgress(); pot.setChanged(); }
            return;
        }
        if (!recipe.getId().toString().equals(pot.activeRecipe)) {
            pot.resetProgress();
            pot.activeRecipe = recipe.getId().toString();
            pot.setChanged();
        }
        var heat = level.getBlockState(pos.below());
        if (!(heat.getBlock() instanceof CampfireBlock) || !heat.getValue(CampfireBlock.LIT) || pot.water < recipe.servings()) return;
        pot.progress++;
        if (pot.progress >= recipe.duration()) {
            pot.inputs.clearContent();
            pot.water -= recipe.servings();
            pot.meal = recipe.assemble(pot.inputs, level.registryAccess());
            pot.servings = recipe.servings();
            pot.resetProgress();
        }
        pot.setChanged();
    }

    public void dropContents() {
        if (level == null) return;
        net.minecraft.world.Containers.dropContents(level, worldPosition, inputs);
        // Unserved food spills when the pot breaks; producing bowl items here would duplicate containers.
        inputs.clearContent();
        meal = ItemStack.EMPTY;
        servings = 0;
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        var slots = net.minecraft.core.NonNullList.withSize(4, ItemStack.EMPTY);
        for (int i = 0; i < 4; i++) slots.set(i, inputs.getItem(i));
        net.minecraft.world.ContainerHelper.saveAllItems(tag, slots);
        tag.putInt("Water", water);
        tag.putInt("Progress", progress);
        tag.putString("ActiveRecipe", activeRecipe);
        tag.put("Meal", meal.save(new CompoundTag()));
        tag.putInt("Servings", servings);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        inputs.clearContent();
        var slots = net.minecraft.core.NonNullList.withSize(4, ItemStack.EMPTY);
        net.minecraft.world.ContainerHelper.loadAllItems(tag, slots);
        for (int i = 0; i < 4; i++) inputs.setItem(i, slots.get(i));
        water = Math.max(0, Math.min(4, tag.getInt("Water")));
        progress = Math.max(0, tag.getInt("Progress"));
        activeRecipe = tag.getString("ActiveRecipe");
        meal = ItemStack.of(tag.getCompound("Meal"));
        servings = meal.isEmpty() ? 0 : Math.max(0, Math.min(4, tag.getInt("Servings")));
    }
}
