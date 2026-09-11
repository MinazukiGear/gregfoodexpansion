package net.mgear.gregfoodexpansion.content.datagen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.content.ContentTables;
import net.mgear.gregfoodexpansion.content.ContentTables.ClasspathSource;

/**
 * runData 入口:内容表 → datagen 一键生成(content-pipeline.md §3)。
 * 首批管线仅语言三件套;物品/方块/配方/标签 provider 随各实装批次补充。
 */
@Mod.EventBusSubscriber(modid = GregFoodExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) throws Exception {
        ContentTables tables = ContentTables.load(new ClasspathSource(
                DataGenerators.class.getClassLoader(), "content"));
        event.getGenerator().addProvider(event.includeClient(),
                ModLangProvider.chinese(event.getGenerator().getPackOutput(), tables));
        event.getGenerator().addProvider(event.includeClient(),
                ModLangProvider.english(event.getGenerator().getPackOutput(), tables));
        event.getGenerator().addProvider(event.includeClient(),
                ModLangProvider.upsideDownEnglish(event.getGenerator().getPackOutput(), tables));
    }
}
