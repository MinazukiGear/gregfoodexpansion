package net.mgear.gregfoodexpansion.data;

import java.util.ArrayList;
import java.util.List;

import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.registry.GFECropBlocks;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.mgear.gregfoodexpansion.registry.GFEWildCropBlocks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

/**
 * 作物掉落表(crop-system-foundation.md §3.4 草案数值,基准):
 * 满龄(AGE 7)产物 ×1 + 25% +1,种子 ×1 + 50% +1、15% +2;未满龄仅种子 ×1。
 * 奖励项各占一条独立奖池,概率由 randomChance 条件承载。
 */
public final class GFELoot {
    private GFELoot() {}

    public static void init(RegistrateLootTableProvider provider) {
        provider.addLootAction(LootContextParamSets.BLOCK, tables -> {
            for (int i = 0; i < GFECropBlocks.ALL_CROPS.size(); i++) {
                var cropBlock = GFECropBlocks.ALL_CROPS.get(i);
                tables.accept(tableId(cropBlock), cropTable(
                        cropBlock.get(),
                        GFECropItems.ALL_PRODUCTS.get(i).get(),
                        GFECropItems.ALL_SEEDS.get(i).get()));
            }
            wildCrops(tables);
        });
    }

    // 野生作物(crop-system-foundation.md §6):种子 ×1 + 50% 加 1,不掉产物,任何工具即采即得。
    private static void wildCrops(java.util.function.BiConsumer<ResourceLocation, LootTable.Builder> tables) {
        for (int i = 0; i < GFEWildCropBlocks.ALL.size(); i++) {
            var wild = GFEWildCropBlocks.ALL.get(i);
            Item seeds = GFECropItems.ALL_SEEDS.get(i).get();
            tables.accept(GregFoodExpansion.id("blocks/" + wild.getId().getPath()),
                    LootTable.lootTable()
                            .withPool(pool(null, seeds, 1.0F, 1))
                            .withPool(pool(null, seeds, 0.5F, 1)));
        }
    }

    private static ResourceLocation tableId(RegistryObject<?> crop) {
        return GregFoodExpansion.id("blocks/" + crop.getId().getPath());
    }

    private static LootTable.Builder cropTable(Block block, Item product, Item seeds) {
        LootItemCondition.Builder fullAge =
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(CropBlock.AGE, CropBlock.MAX_AGE));
        return LootTable.lootTable()
                .withPool(pool(fullAge, product, 1.0F, 1))
                .withPool(pool(fullAge, product, 0.25F, 1))
                .withPool(pool(null, seeds, 1.0F, 1))
                .withPool(pool(fullAge, seeds, 0.5F, 1))
                .withPool(pool(fullAge, seeds, 0.15F, 2));
    }

    // 一条独立奖池:condition 通过时掉 1 份 item;chance<1 时叠加 randomChance 条件,
    // count>1 时套 set_count(概率奖励池,如"15% 概率 +2")。
    private static LootPool.Builder pool(LootItemCondition.Builder condition, Item item,
                                         float chance, int count) {
        List<LootItemCondition.Builder> conditions = new ArrayList<>();
        if (condition != null) {
            conditions.add(condition);
        }
        if (chance < 1.0F) {
            conditions.add(LootItemRandomChanceCondition.randomChance(chance));
        }
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item));
        if (count != 1) {
            pool.apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)));
        }
        for (LootItemCondition.Builder c : conditions) {
            pool.when(c);
        }
        return pool;
    }
}
