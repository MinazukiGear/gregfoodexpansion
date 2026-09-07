package net.mgear.gregfoodexpansion;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.mojang.logging.LogUtils;
import com.tterrag.registrate.providers.ProviderType;
import net.mgear.gregfoodexpansion.cooking.GFEDishes;
import net.mgear.gregfoodexpansion.data.GFEBlockStates;
import net.mgear.gregfoodexpansion.data.GFEBlockTags;
import net.mgear.gregfoodexpansion.data.GFEItemModels;
import net.mgear.gregfoodexpansion.data.GFEItemTags;
import net.mgear.gregfoodexpansion.data.GFELoot;
import net.mgear.gregfoodexpansion.data.GFERecipes;
import net.mgear.gregfoodexpansion.prep.GFEFormItems;
import net.mgear.gregfoodexpansion.prep.GFEPrepTools;
import net.mgear.gregfoodexpansion.registry.GFECreativeModeTabs;
import net.mgear.gregfoodexpansion.registry.GFECropBlocks;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.mgear.gregfoodexpansion.registry.GFECropLootModifiers;
import net.mgear.gregfoodexpansion.registry.GFEMachines;
import net.mgear.gregfoodexpansion.registry.GFERecipeTypes;
import net.mgear.gregfoodexpansion.registry.GFERegistration;
import net.mgear.gregfoodexpansion.registry.GTFEMaterials;
import net.mgear.gregfoodexpansion.registry.GFEWildCropBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
        GFECropBlocks.BLOCKS.register(modEventBus);
        GFEWildCropBlocks.BLOCKS.register(modEventBus);
        GFECropItems.ITEMS.register(modEventBus);
        GFEFormItems.ITEMS.register(modEventBus);
        GFEPrepTools.ITEMS.register(modEventBus);
        GFEDishes.ITEMS.register(modEventBus);
        GFECropLootModifiers.SERIALIZERS.register(modEventBus);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.RECIPE, GFERecipes::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.BLOCKSTATE, GFEBlockStates::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.ITEM_MODEL, GFEItemModels::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.LOOT, GFELoot::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, GFEBlockTags::init);
        GFERegistration.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, GFEItemTags::init);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modEventBus.addListener(GTFEMaterials::createRegistry);
        modEventBus.addListener(GTFEMaterials::register);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
    }

    private void registerMachines(final GTCEuAPI.RegisterEvent<?, MachineDefinition> event) {
        GFEMachines.init();
    }

    private void registerRecipeTypes(final GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        GFERecipeTypes.init(event);
    }

    // 创造标签展示序:种子 → 作物产物 → 食材形态 → 手工工具
    // (crop-system-foundation.md §2 / food-processor.md §2);
    // 切配机经 registrate 默认标签自动追加在末尾。
    private void addCreative(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == GFECreativeModeTabs.MAIN.getKey()) {
            GFECropItems.ALL_SEEDS.forEach(item -> event.accept(item.get()));
            GFECropItems.ALL_PRODUCTS.forEach(item -> event.accept(item.get()));
            GFEFormItems.ALL.forEach(item -> event.accept(item.get()));
            GFEPrepTools.ALL.forEach(item -> event.accept(item.get()));
            GFEDishes.ALL.forEach(item -> event.accept(item.get()));
        }
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
