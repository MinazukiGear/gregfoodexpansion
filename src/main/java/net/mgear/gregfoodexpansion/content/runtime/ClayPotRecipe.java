package net.mgear.gregfoodexpansion.content.runtime;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;

/** Four unordered, single-item inputs; water is charged separately in bowl units. */
public record ClayPotRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients,
                            ItemStack result, int duration, int servings) implements Recipe<Container> {
    public ClayPotRecipe {
        if (ingredients.isEmpty() || ingredients.size() > 4 || result.isEmpty() || result.getCount() != 1
                || !result.isEdible() || duration < 1 || duration > 72000 || servings < 2 || servings > 4) {
            throw new JsonParseException("Invalid clay pot recipe: " + id);
        }
    }
    @Override public boolean matches(Container container, Level level) {
        var inputs = new ArrayList<ItemStack>();
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (!container.getItem(slot).isEmpty()) inputs.add(container.getItem(slot));
        }
        return inputs.size() == ingredients.size() && RecipeMatcher.findMatches(inputs, ingredients) != null;
    }
    @Override public ItemStack assemble(Container container, RegistryAccess access) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= ingredients.size(); }
    @Override public ItemStack getResultItem(RegistryAccess access) { return result.copy(); }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return GFContent.POT_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return GFContent.POT_RECIPE_TYPE.get(); }
    @Override public NonNullList<Ingredient> getIngredients() { return ingredients; }
    @Override public ItemStack getToastSymbol() { return new ItemStack(GFContent.CLAY_POT.get()); }

    public static final class Serializer implements RecipeSerializer<ClayPotRecipe> {
        @Override public ClayPotRecipe fromJson(ResourceLocation id, JsonObject json) {
            var ingredients = NonNullList.<Ingredient>create();
            for (var element : GsonHelper.getAsJsonArray(json, "ingredients")) ingredients.add(Ingredient.fromJson(element));
            return new ClayPotRecipe(id, ingredients, ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                    GsonHelper.getAsInt(json, "duration"), GsonHelper.getAsInt(json, "servings"));
        }
        @Override public ClayPotRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            if (size < 1 || size > 4) throw new IllegalArgumentException("Invalid pot ingredient count");
            var ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) ingredients.set(i, Ingredient.fromNetwork(buffer));
            return new ClayPotRecipe(id, ingredients, buffer.readItem(), buffer.readVarInt(), buffer.readVarInt());
        }
        @Override public void toNetwork(FriendlyByteBuf buffer, ClayPotRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            recipe.ingredients.forEach(i -> i.toNetwork(buffer));
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.duration);
            buffer.writeVarInt(recipe.servings);
        }
    }
}
