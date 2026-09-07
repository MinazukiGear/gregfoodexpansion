package net.mgear.gregfoodexpansion.loot;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.RegistryObject;

/**
 * 草丛种子掉落(crop-system-foundation.md §6):每次破坏草丛 12.5% 概率追加一枚种子
 * (与原版小麦种子总概率持平),池内按稀有度加权:常见 20 / 少见 10 / 稀有 6 / 珍稀 4
 * (总权重 146)。目标表由 modifier JSON 的 forge:loot_table_id 条件圈定
 * (minecraft:blocks/grass),不覆写原版掉落表。
 */
public final class CropSeedDropModifier extends LootModifier {
    public static final Codec<CropSeedDropModifier> CODEC = RecordCodecBuilder.create(
            instance -> codecStart(instance).apply(instance, CropSeedDropModifier::new));

    private record WeightedSeed(RegistryObject<Item> item, int weight) {
    }

    private static final List<WeightedSeed> TABLE = List.of(
            new WeightedSeed(GFECropItems.SOYBEAN_SEEDS, 20),
            new WeightedSeed(GFECropItems.CORN_SEEDS, 20),
            new WeightedSeed(GFECropItems.ONION_SEEDS, 20),
            new WeightedSeed(GFECropItems.CABBAGE_SEEDS, 20),
            new WeightedSeed(GFECropItems.TOMATO_SEEDS, 10),
            new WeightedSeed(GFECropItems.RICE_SEEDS, 10),
            new WeightedSeed(GFECropItems.BARLEY_SEEDS, 10),
            new WeightedSeed(GFECropItems.PEANUT_SEEDS, 10),
            new WeightedSeed(GFECropItems.CHILI_SEEDS, 6),
            new WeightedSeed(GFECropItems.TEA_SEEDS, 6),
            new WeightedSeed(GFECropItems.HOPS_SEEDS, 6),
            new WeightedSeed(GFECropItems.GRAPE_SEEDS, 4),
            new WeightedSeed(GFECropItems.COFFEE_SEEDS, 4));

    private static final int TOTAL_WEIGHT =
            TABLE.stream().mapToInt(WeightedSeed::weight).sum();

    public CropSeedDropModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
                                                 LootContext context) {
        if (context.getRandom().nextFloat() < 0.125F) {
            generatedLoot.add(new ItemStack(pickSeed(context.getRandom())));
        }
        return generatedLoot;
    }

    private static Item pickSeed(RandomSource random) {
        int roll = random.nextInt(TOTAL_WEIGHT);
        for (WeightedSeed entry : TABLE) {
            roll -= entry.weight();
            if (roll < 0) {
                return entry.item().get();
            }
        }
        throw new IllegalStateException("Unreachable weighted seed pick");
    }
}
