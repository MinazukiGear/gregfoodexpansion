package net.mgear.gregfoodexpansion.content.runtime;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mgear.gregfoodexpansion.content.ContentIds;
import net.mgear.gregfoodexpansion.content.ContentTables;

public final class GFContent {
    public static final ContentTables TABLES = loadTables();
    public static final ContentIds IDS = new ContentIds(TABLES);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ContentIds.MOD);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ContentIds.MOD);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ContentIds.MOD);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ContentIds.MOD);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ContentIds.MOD);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ContentIds.MOD);
    public static final Map<String, RegistryObject<Item>> ITEM_ENTRIES = new LinkedHashMap<>();
    public static final Map<String, RegistryObject<GFCropBlock>> CROPS = new LinkedHashMap<>();
    public static final RegistryObject<ClayPotBlock> CLAY_POT = BLOCKS.register("clay_pot",
            () -> new ClayPotBlock(Block.Properties.copy(Blocks.TERRACOTTA).strength(1.5F).noOcclusion()));
    public static final RegistryObject<BlockEntityType<ClayPotBlockEntity>> POT_ENTITY = BLOCK_ENTITIES.register("clay_pot",
            () -> BlockEntityType.Builder.of(ClayPotBlockEntity::new, CLAY_POT.get()).build(null));
    public static final RegistryObject<RecipeType<ClayPotRecipe>> POT_RECIPE_TYPE = RECIPE_TYPES.register("clay_pot_cooking",
            () -> new RecipeType<>() { @Override public String toString() { return ContentIds.MOD + ":clay_pot_cooking"; } });
    public static final RegistryObject<RecipeSerializer<ClayPotRecipe>> POT_SERIALIZER = SERIALIZERS.register("clay_pot_cooking",
            ClayPotRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        var errors = new net.mgear.gregfoodexpansion.content.GameplayAudit(TABLES).errors();
        if (!errors.isEmpty()) throw new IllegalStateException("Invalid gameplay tables: " + String.join("; ", errors));
        for (String crop : TABLES.gameplay.cultivation()) {
            CROPS.put(crop, BLOCKS.register(crop + "_crop",
                    () -> new GFCropBlock(Block.Properties.copy(Blocks.WHEAT), () -> item(crop + "_seeds"))));
        }
        IDS.ownedItems().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            String id = entry.getKey().substring(ContentIds.MOD.length() + 1);
            String ref = entry.getValue();
            ITEM_ENTRIES.put(id, ITEMS.register(id, () -> createItem(id, ref)));
        });
        TABS.register("food", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.gregfoodexpansion"))
                .icon(() -> new ItemStack(item("tomato-egg-noodles")))
                .displayItems((parameters, output) -> ITEM_ENTRIES.values().forEach(i -> output.accept(i.get())))
                .build());
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
        TABS.register(bus);
    }

    private static Item createItem(String id, String ref) {
        if (ref.startsWith("seed:")) return new ItemNameBlockItem(CROPS.get(ref.substring(5)).get(), new Item.Properties());
        if (id.equals("clay_pot")) return new BlockItem(CLAY_POT.get(), new Item.Properties()) {
            @Override public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level level,
                    java.util.List<Component> lines, TooltipFlag flag) {
                lines.add(Component.translatable("gregfoodexpansion.pot.help"));
                lines.add(Component.translatable("gregfoodexpansion.pot.recipe"));
            }
        };
        if (ContentIds.TOOLS.contains(id)) return new HandToolItem();
        if (id.equals("water_bowl")) return new Item(new Item.Properties().stacksTo(1).craftRemainder(Items.BOWL));
        var food = TABLES.gameplay.potDishes().stream().filter(d -> d.id().equals(id)).findFirst();
        if (food.isPresent()) {
            var dish = food.get();
            return new BowlFoodItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder()
                    .nutrition(dish.nutrition()).saturationMod(dish.saturation()).build()));
        }
        var machineFood = TABLES.gameplay.machineDishes().stream().filter(d -> d.id().equals(id)).findFirst();
        if (machineFood.isPresent()) {
            var dish = machineFood.get();
            return new BowlFoodItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder()
                    .nutrition(dish.nutrition()).saturationMod(dish.saturation()).build()));
        }
        if (ref.startsWith("dish:")) return new Item(new Item.Properties()) {
            @Override public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level level,
                    java.util.List<Component> lines, TooltipFlag flag) {
                lines.add(Component.translatable("gregfoodexpansion.food.pending").withStyle(net.minecraft.ChatFormatting.GRAY));
            }
        };
        return new Item(new Item.Properties());
    }

    public static Item item(String id) { return ITEM_ENTRIES.get(id).get(); }

    private static ContentTables loadTables() {
        try {
            return ContentTables.load(new ContentTables.ClasspathSource(GFContent.class.getClassLoader(), "content"));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load GFE content tables", e);
        }
    }
}
