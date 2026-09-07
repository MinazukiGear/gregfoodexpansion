package net.mgear.gregfoodexpansion;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.mgear.gregfoodexpansion.data.GFELang;
import net.mgear.gregfoodexpansion.registry.GFERegistration;

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
    public String addonModId() {
        return GregFoodExpansion.MOD_ID;
    }
}
