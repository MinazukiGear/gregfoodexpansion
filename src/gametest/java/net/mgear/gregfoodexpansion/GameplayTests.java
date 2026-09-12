package net.mgear.gregfoodexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import net.mgear.gregfoodexpansion.content.runtime.*;

@GameTestHolder("gregfoodexpansion")
@PrefixGameTestTemplate(false)
public final class GameplayTests {
    private static final BlockPos POT = new BlockPos(2, 2, 2);

    @GameTest(template = "empty")
    public static void registrationAndAliases(GameTestHelper h) {
        String flipped = net.mgear.gregfoodexpansion.content.lang.EnglishUpsideDown.flip("%s has %s servings");
        h.assertTrue(flipped.indexOf("%2$s") < flipped.indexOf("%1$s") && flipped.contains("%1$s"), "Upside-down locale must preserve argument identities");
        GFContent.IDS.ownedItems().forEach((id, ref) -> h.assertTrue(ForgeRegistries.ITEMS.containsKey(ResourceLocation.parse(id)), "Missing runtime item " + id));
        for (String machine : java.util.List.of("prep_workshop", "cooking_workshop", "tunnel_oven")) {
            h.assertTrue(ForgeRegistries.BLOCKS.containsKey(GregFoodExpansion.id(machine)), "Machine block missing: " + machine);
            h.assertTrue(ForgeRegistries.ITEMS.containsKey(GregFoodExpansion.id(machine)), "Machine item missing: " + machine);
        }
        h.assertTrue(GFContent.IDS.item("animal:egg").equals("minecraft:egg"), "Egg must reuse vanilla Item");
        h.assertTrue(!ForgeRegistries.ITEMS.containsKey(GregFoodExpansion.id("egg")), "Duplicate egg registered");
        h.assertTrue(GFContent.item("tomato-egg-noodles").isEdible(), "Implemented dish must be edible");
        h.assertTrue(!GFContent.item("yangchun-noodles").isEdible(), "Pending recipe must not claim implementation");
        h.assertTrue(h.getLevel().getRecipeManager().getAllRecipesFor(GFContent.POT_RECIPE_TYPE.get()).size() == 1, "Unexpected pot recipe count");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void handChainAndRemainders(GameTestHelper h) {
        var mortar = craft(h, "mortar", new ItemStack(Items.COBBLESTONE), new ItemStack(Items.COBBLESTONE), new ItemStack(Items.STICK));
        mortar.setDamageValue(7);
        var flour = craft(h, "flour", new ItemStack(Items.WHEAT), mortar);
        var dough = craft(h, "dough", flour, new ItemStack(GFContent.item("water_bowl")));
        var pin = craft(h, "rolling_pin", new ItemStack(Items.STICK), new ItemStack(Items.STICK), new ItemStack(Items.STICK));
        var knife = craft(h, "kitchen_knife", new ItemStack(Items.FLINT), new ItemStack(Items.STICK));
        h.assertTrue(craft(h, "noodles", dough, pin, knife).is(GFContent.item("noodles")), "Hand chain must yield noodles");
        var broken = new ItemStack(GFContent.item("mortar"));
        broken.setDamageValue(broken.getMaxDamage() - 1);
        h.assertTrue(broken.getCraftingRemainingItem().isEmpty(), "Broken tool must not be returned");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void waterSourceBowlBridge(GameTestHelper h) {
        var water = new BlockPos(2, 2, 2);
        h.setBlock(water.below(), Blocks.DIRT);
        h.setBlock(water, Blocks.WATER);
        var player = h.makeMockSurvivalPlayer();
        var stand = h.absolutePos(new BlockPos(2, 2, 5));
        player.setPos(stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5);
        player.setYRot(180);
        player.setXRot(30);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOWL, 2));
        var event = new net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem(player, InteractionHand.MAIN_HAND);
        ManualFoodEvents.fillBowl(event);
        h.assertTrue(event.isCanceled(), "Water source interaction was not handled");
        h.assertTrue(player.getMainHandItem().is(Items.BOWL) && player.getMainHandItem().getCount() == 1,
                "Filling must consume exactly one empty bowl");
        h.assertTrue(player.getInventory().countItem(GFContent.item("water_bowl")) == 1, "Filled bowl missing");
        h.assertTrue(h.getBlockState(water).is(Blocks.WATER), "Manual bridge must preserve the water source");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void breakingCookedPotDoesNotCreateBowls(GameTestHelper h) {
        var pot = pot(h, true);
        var player = h.makeMockSurvivalPlayer();
        ingredients(pot, player);
        insert(pot, player, GFContent.item("water_bowl"));
        insert(pot, player, GFContent.item("water_bowl"));
        ticks(h, pot, 1200);
        h.assertTrue(pot.servings() == 2, "Test pot must contain cooked food");
        h.destroyBlock(POT);
        h.assertItemEntityNotPresent(GFContent.item("tomato-egg-noodles"), POT, 3);
        h.assertItemEntityNotPresent(Items.BOWL, POT, 3);
        h.succeed();
    }

    private static ItemStack craft(GameTestHelper h, String name, ItemStack... inputs) {
        var grid = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override public ItemStack quickMoveStack(Player p, int slot) { return ItemStack.EMPTY; }
            @Override public boolean stillValid(Player p) { return true; }
        }, 3, 3);
        for (int i=0; i<inputs.length; i++) grid.setItem(i, inputs[i]);
        var recipe = h.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, h.getLevel()).orElseThrow();
        h.assertTrue(recipe.getId().equals(GregFoodExpansion.id("hand/" + name)), "Wrong or conflicting recipe: " + recipe.getId());
        var remainders = recipe.getRemainingItems(grid);
        for (int i=0; i<inputs.length; i++) {
            if (inputs[i].getItem() instanceof HandToolItem) {
                h.assertTrue(remainders.get(i).is(inputs[i].getItem()) && remainders.get(i).getDamageValue() == inputs[i].getDamageValue() + 1,
                        "Tool durability must survive crafting");
            }
            if (inputs[i].is(GFContent.item("water_bowl"))) h.assertTrue(remainders.get(i).is(Items.BOWL), "Dough must return water bowl");
        }
        return recipe.assemble(grid, h.getLevel().registryAccess());
    }

    private static ClayPotBlockEntity pot(GameTestHelper h, boolean lit) {
        h.setBlock(POT.below(), Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, lit));
        h.setBlock(POT, GFContent.CLAY_POT.get());
        return (ClayPotBlockEntity)h.getBlockEntity(POT);
    }
    private static void insert(ClayPotBlockEntity pot, Player player, Item item) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));
        pot.interact(player, InteractionHand.MAIN_HAND);
    }
    private static void ingredients(ClayPotBlockEntity pot, Player player) {
        insert(pot, player, GFContent.item("scallion")); // Deliberately unordered.
        insert(pot, player, Items.EGG);
        insert(pot, player, GFContent.item("noodles"));
        insert(pot, player, GFContent.item("tomato"));
    }
    private static void ticks(GameTestHelper h, ClayPotBlockEntity pot, int count) {
        for (int i=0; i<count; i++) ClayPotBlockEntity.tick(h.getLevel(), h.absolutePos(POT), h.getBlockState(POT), pot);
    }

    @GameTest(template = "empty")
    public static void potHeatWaterSaveAndServe(GameTestHelper h) {
        var pot = pot(h, false);
        var player = h.makeMockSurvivalPlayer();
        ingredients(pot, player);
        insert(pot, player, GFContent.item("water_bowl"));
        h.assertTrue(player.getMainHandItem().is(Items.BOWL), "Filling must return a bowl");
        ticks(h, pot, 10);
        h.assertTrue(pot.progress() == 0, "Cold pot must not cook");
        h.setBlock(POT.below(), Blocks.CAMPFIRE);
        ticks(h, pot, 10);
        h.assertTrue(pot.progress() == 0, "One bowl of water must not make two servings");
        insert(pot, player, GFContent.item("water_bowl"));
        ticks(h, pot, 400);
        h.assertTrue(pot.progress() == 400, "Cooking did not advance");
        var saved = pot.saveWithFullMetadata();
        var restored = new ClayPotBlockEntity(h.absolutePos(POT), h.getBlockState(POT));
        restored.load(saved);
        h.getLevel().setBlockEntity(restored);
        h.assertTrue(restored.progress() == 400 && restored.water() == 2, "Progress or water lost on reload");
        ticks(h, restored, 800);
        h.assertTrue(restored.servings() == 2 && restored.inputs().isEmpty() && restored.water() == 0, "Recipe did not consume inputs exactly once");
        var done = restored.saveWithFullMetadata();
        restored.load(done);
        for (int i=0; i<2; i++) {
            insert(restored, player, Items.BOWL);
            var meal = player.getMainHandItem();
            h.assertTrue(meal.is(GFContent.item("tomato-egg-noodles")), "Serving needs an edible dish");
            player.getFoodData().setFoodLevel(10);
            var empty = meal.finishUsingItem(h.getLevel(), player);
            h.assertTrue(empty.is(Items.BOWL) && player.getFoodData().getFoodLevel() == 15, "Eating must feed the player and return the bowl");
        }
        insert(restored, player, Items.BOWL);
        h.assertTrue(player.getMainHandItem().is(Items.BOWL) && restored.servings() == 0, "Third serving duplicated");
        h.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void actualTickerAndExtinguish(GameTestHelper h) {
        var pot = pot(h, true);
        var player = h.makeMockSurvivalPlayer();
        ingredients(pot, player);
        insert(pot, player, GFContent.item("water_bowl"));
        insert(pot, player, GFContent.item("water_bowl"));
        h.runAfterDelay(10, () -> {
            h.assertTrue(pot.progress() > 0, "Block entity ticker is not connected");
            int progress = pot.progress();
            h.setBlock(POT.below(), Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, false));
            h.runAfterDelay(10, () -> {
                h.assertTrue(pot.progress() == progress, "Extinguished campfire must pause cooking");
                h.succeed();
            });
        });
    }

    @GameTest(template = "empty")
    public static void wrongInputsAndRetrieval(GameTestHelper h) {
        var pot = pot(h, true);
        var player = h.makeMockSurvivalPlayer();
        ingredients(pot, player);
        insert(pot, player, GFContent.item("water_bowl"));
        insert(pot, player, GFContent.item("water_bowl"));
        pot.inputs().setItem(1, new ItemStack(Items.ROTTEN_FLESH));
        ticks(h, pot, 1300);
        h.assertTrue(pot.servings() == 0 && pot.water() == 2, "Wrong ingredients must not produce food");
        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        pot.interact(player, InteractionHand.MAIN_HAND);
        h.assertTrue(player.getMainHandItem().is(GFContent.item("tomato")), "Sneaking must retrieve last ingredient");
        var saved = pot.saveWithFullMetadata();
        pot.load(saved);
        h.assertTrue(pot.inputs().getItem(1).is(Items.ROTTEN_FLESH) && pot.inputs().getItem(3).isEmpty(), "Reload must preserve input slots");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void cropsGrowAndDropRenewableSeeds(GameTestHelper h) {
        var pos = new BlockPos(4, 2, 4);
        for (String name : GFContent.TABLES.gameplay.cultivation()) {
            var crop = GFContent.CROPS.get(name).get();
            h.setBlock(pos.below(), Blocks.FARMLAND);
            h.setBlock(pos, crop);
            for (int i=0; i<4; i++) crop.performBonemeal(h.getLevel(), h.getLevel().random, h.absolutePos(pos), h.getBlockState(pos));
            h.assertTrue(h.getBlockState(pos).getValue(CropBlock.AGE) == 7, "Crop cannot mature: " + name);
            var drops = Block.getDrops(h.getBlockState(pos), h.getLevel(), h.absolutePos(pos), null);
            h.assertTrue(drops.stream().filter(s -> s.is(GFContent.item(name + "_seeds"))).mapToInt(ItemStack::getCount).sum() >= 2, "Seeds must be renewable");
            h.assertTrue(drops.stream().anyMatch(s -> s.is(GFContent.item(name))), "Mature crop must yield produce");
            h.setBlock(pos, Blocks.AIR);
        }
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void wildFeaturesLoadAndPlace(GameTestHelper h) {
        for (int x=1; x<13; x++) for (int z=1; z<13; z++) h.setBlock(new BlockPos(x,1,z), Blocks.DIRT);
        var registry = h.getLevel().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        for (String name : GFContent.TABLES.gameplay.cultivation()) {
            for (int x=1; x<13; x++) for (int z=1; z<13; z++) h.setBlock(new BlockPos(x,2,z), Blocks.AIR);
            var origin = h.absolutePos(new BlockPos(7, 2, 7));
            var wild = GFContent.CROPS.get(name).get().defaultBlockState().setValue(GFCropBlock.WILD, true).setValue(CropBlock.AGE, 7);
            h.assertTrue(h.getLevel().isEmptyBlock(origin), "Wild feature test origin must be air: " + h.getLevel().getBlockState(origin));
            h.assertTrue(wild.canSurvive(h.getLevel(), origin), "Wild crop cannot survive on dirt; sky=" + h.getLevel().canSeeSky(origin)
                    + ", light=" + h.getLevel().getRawBrightness(origin, 0) + ", ground=" + h.getLevel().getBlockState(origin.below()));
            var feature = registry.getHolderOrThrow(ResourceKey.create(Registries.CONFIGURED_FEATURE, GregFoodExpansion.id("wild_" + name))).value();
            for (int i=0; i<8; i++) feature.place(h.getLevel(), h.getLevel().getChunkSource().getGenerator(), RandomSource.create(81+i), h.absolutePos(new BlockPos(7,2,7)));
            boolean found = false;
            for (int x=1; x<13; x++) for (int z=1; z<13; z++) {
                var state = h.getBlockState(new BlockPos(x,2,z));
                if (state.is(GFContent.CROPS.get(name).get())) found = state.getValue(GFCropBlock.WILD) && state.getValue(CropBlock.AGE) == 7 || found;
            }
            h.assertTrue(found, "Wild crop feature did not generate " + name);
        }
        h.succeed();
    }
}
