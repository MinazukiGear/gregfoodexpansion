package net.mgear.gregfoodexpansion.registry;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class GFECreativeModeTabs {

    // 标签图标 = 首批作物的大豆(crop-system-foundation.md §2)。
    public static final RegistryEntry<CreativeModeTab> MAIN = GFERegistration.REGISTRATE.defaultCreativeTab("main",
            builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("main",
                            GFERegistration.REGISTRATE))
                    .icon(() -> new ItemStack(GFECropItems.SOYBEAN.get()))
                    .title(GFERegistration.REGISTRATE.addLang("itemGroup", GregFoodExpansion.id("main"),
                            GregFoodExpansion.MOD_NAME))
                    .build())
            .register();

    public static void init() {}

    private GFECreativeModeTabs() {}
}
