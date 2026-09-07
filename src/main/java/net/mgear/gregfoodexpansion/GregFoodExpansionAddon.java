package net.mgear.gregfoodexpansion;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.mgear.gregfoodexpansion.data.GFECookingRecipes;
import net.mgear.gregfoodexpansion.data.GFEPrepRecipes;
import net.mgear.gregfoodexpansion.data.GFELang;
import net.mgear.gregfoodexpansion.registry.GFERegistration;
import net.minecraft.data.recipes.FinishedRecipe;

@GTAddon
public final class GregFoodExpansionAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GFERegistration.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        GFELang.init();
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GFEPrepRecipes.init(provider);
        GFECookingRecipes.init(provider);
    }

    @Override
    public String addonModId() {
        return GregFoodExpansion.MOD_ID;
    }
}
