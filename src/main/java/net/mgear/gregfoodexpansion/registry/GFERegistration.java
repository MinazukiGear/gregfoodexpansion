package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.mgear.gregfoodexpansion.GregFoodExpansion;

public final class GFERegistration {
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GregFoodExpansion.MOD_ID);

    private GFERegistration() {}
}
