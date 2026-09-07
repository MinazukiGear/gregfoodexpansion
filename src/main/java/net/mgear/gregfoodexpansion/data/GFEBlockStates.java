package net.mgear.gregfoodexpansion.data;

import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.crop.GFECropBlock;
import net.mgear.gregfoodexpansion.registry.GFECropBlocks;
import net.mgear.gregfoodexpansion.registry.GFEWildCropBlocks;
import net.mgear.gregfoodexpansion.registry.GFEBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

public final class GFEBlockStates {
    private GFEBlockStates() {}

    // age→灰度模板映射:0,1,1,2,2,3,3,3(crop-system-foundation.md §5)。
    private static final int[] AGE_TO_STAGE = {0, 1, 1, 2, 2, 3, 3, 3};

    public static void init(RegistrateBlockstateProvider provider) {
        ovenStructures(provider);
        GFECropBlocks.ALL_CROPS.forEach(crop -> {
            GFECropBlock block = (GFECropBlock) crop.get();
            String name = crop.getId().getPath();
            ModelFile[] stages = new ModelFile[4];
            for (int stage = 0; stage < 4; stage++) {
                stages[stage] = provider.models()
                        .crop(name + "_stage_" + stage, modLoc("block/crop/stage_" + stage));
            }
            var builder = provider.getVariantBuilder(block);
            for (int age = 0; age < 8; age++) {
                builder.partialState()
                        .with(CropBlock.AGE, age)
                        .setModels(new ConfiguredModel(stages[AGE_TO_STAGE[age]]));
            }
        });
        wildCrops(provider);
    }

    // 野生作物:复用成熟档灰度模板染色(cross 模板带 tintindex),零新贴图。
    private static void wildCrops(RegistrateBlockstateProvider provider) {
        GFEWildCropBlocks.ALL.forEach(crop -> {
            ModelFile model = provider.models()
                    .cross(crop.getId().getPath(), modLoc("block/crop/stage_3"));
            provider.getVariantBuilder(crop.get())
                    .partialState()
                    .setModels(new ConfiguredModel(model));
        });
    }

    // 烘焙结构方块:全 cube 模型(贴图区分外壳/传送带/加热管/排气口)
    private static void ovenStructures(RegistrateBlockstateProvider provider) {
        GFEBlocks.ALL.forEach(pair -> {
            var block = pair.getSecond();
            ModelFile model = provider.models()
                    .cubeAll(block.getId().getPath(), modLoc("block/casings/" + block.getId().getPath()));
            provider.getVariantBuilder(block.get())
                    .partialState()
                    .setModels(new ConfiguredModel(model));
        });
    }

    private static ResourceLocation modLoc(String path) {
        return GregFoodExpansion.id(path);
    }
}
