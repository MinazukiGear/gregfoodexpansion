package net.mgear.gregfoodexpansion;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.providers.ProviderType;
import net.mgear.gregfoodexpansion.data.GFEBlockStates;
import net.mgear.gregfoodexpansion.data.GFEBlockTags;
import net.mgear.gregfoodexpansion.data.GFEItemModels;
import net.mgear.gregfoodexpansion.data.GFEItemTags;
import net.mgear.gregfoodexpansion.data.GFELoot;
import net.mgear.gregfoodexpansion.data.GFERecipes;
import net.mgear.gregfoodexpansion.registry.GFECreativeModeTabs;
import net.mgear.gregfoodexpansion.registry.GFERegistration;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(GregFoodExpansion.MOD_ID)
public final class GregFoodExpansion {
    public static final String MOD_ID = "gregfoodexpansion";
    public static final String MOD_NAME = "Greg Food Expansion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GregFoodExpansion(final FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        GFERegistration.REGISTRATE.registerEventListeners(modEventBus);
        GFECreativeModeTabs.init();
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.RECIPE, GFERecipes::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.BLOCKSTATE, GFEBlockStates::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.ITEM_MODEL, GFEItemModels::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.LOOT, GFELoot::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, GFEBlockTags::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, GFEItemTags::init);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("{} initialized with GTCEu {}.", MOD_NAME, loadedVersion("gtceu"));
    }

    private static String loadedVersion(final String modId) {
        return ModList.get()
                .getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("not loaded on this side");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /** gtceu 命名空间资源 ID:仅用于精确引用上游注册对象(材料/机器/配方)。 */
    public static ResourceLocation gtceuId(String path) {
        return ResourceLocation.fromNamespaceAndPath("gtceu", path);
    }
}
