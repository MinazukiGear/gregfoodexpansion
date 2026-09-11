package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.mgear.gregfoodexpansion.GregFoodExpansion;

/**
 * 附属 Registrate 持有器:机器/多方块注册入口,
 * {@code GregFoodExpansionAddon#getRegistrate()} 返回同一实例。
 */
public final class GFERegistration {
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GregFoodExpansion.MOD_ID);

    private GFERegistration() {}
}
