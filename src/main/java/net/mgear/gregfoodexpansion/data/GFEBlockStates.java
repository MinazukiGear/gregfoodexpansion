package net.mgear.gregfoodexpansion.data;

import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.crop.GFECropBlock;
import net.mgear.gregfoodexpansion.registry.GFECropBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

public final class GFEBlockStates {
    private GFEBlockStates() {}

    // age→灰度模板映射:0,1,1,2,2,3,3,3(crop-system-foundation.md §5)。
    private static final int[] AGE_TO_STAGE = {0, 1, 1, 2, 2, 3, 3, 3};

    public static void init(RegistrateBlockstateProvider provider) {
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
    }

    private static ResourceLocation modLoc(String path) {
        return GregFoodExpansion.id(path);
    }
}
