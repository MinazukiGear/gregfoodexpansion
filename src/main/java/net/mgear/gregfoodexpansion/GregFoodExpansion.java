package net.mgear.gregfoodexpansion;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * 模组入口。设计文档 v2 重置后为空骨架:内容注册随各专项文档实装批次重建,
 * 架构与批次见 docs/design/(总纲:overview.md)。
 */
@Mod(GregFoodExpansion.MOD_ID)
public final class GregFoodExpansion {
    public static final String MOD_ID = "gregfoodexpansion";
    public static final String MOD_NAME = "Greg Food Expansion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GregFoodExpansion(final FMLJavaModLoadingContext context) {
        var modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
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
