package net.mgear.gregfoodexpansion;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.common.machine.multiblock.part.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.gametest.*;
import net.mgear.gregfoodexpansion.content.runtime.*;
import net.mgear.gregfoodexpansion.registry.*;

@GameTestHolder("gregfoodexpansion")
@PrefixGameTestTemplate(false)
public final class WorkshopTests {
    private static final BlockPos ORIGIN = new BlockPos(2, 1, 2);

    @GameTest(template = "empty")
    public static void structuresRequireCompleteSegmentsAndHatches(GameTestHelper h) {
        for (boolean cooking : new boolean[]{false, true}) {
            for (int count : new int[]{0, 1, 4}) {
                var machine = build(h, cooking, count);
                h.assertTrue(machine.checkPatternWithLock(), "Preview does not form: cooking=" + cooking + ", segments=" + count);
                machine.onStructureFormed();
                h.assertTrue(machine.segments() == count, "Incorrect structure segment count");
                machine.onStructureInvalid();
            }
        }
        var machine = build(h, true, 1);
        h.setBlock(ORIGIN.offset(1, 1, 2), GTBlocks.CASING_STEEL_GEARBOX.get());
        h.assertTrue(!machine.checkPatternWithLock(), "Rolling hardware must not satisfy a boiling segment");
        h.setBlock(ORIGIN.offset(1, 1, 2), WorkshopPatterns.moduleBlock(true));
        h.setBlock(ORIGIN.offset(1, 2, 1), GTBlocks.CASING_STEEL_SOLID.get());
        h.assertTrue(!machine.checkPatternWithLock(), "Cooking must require a fluid input hatch");
        h.setBlock(ORIGIN.offset(1, 2, 1), GTMachines.FLUID_IMPORT_HATCH[GTValues.LV].getBlock());
        h.setBlock(ORIGIN.offset(0, 1, 1), GTBlocks.CASING_STEEL_SOLID.get());
        h.assertTrue(!machine.checkPatternWithLock(), "Five ingredient types need at least two LV input buses");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void moduleAndVoltageGateAndParallelLimits(GameTestHelper h) {
        var recipe = recipe(h, "prep_workshop/rolled_noodles");
        var bare = form(h, false, 0);
        feed(bare, new ItemStack(GFContent.item("dough"), 4));
        h.assertTrue(bare.fullModifyRecipe(recipe) == null, "Bare workshop must not process noodles");
        bare.onStructureInvalid();

        var machine = form(h, false, 4);
        feed(machine, new ItemStack(GFContent.item("dough"), 2));
        var modified = machine.fullModifyRecipe(recipe);
        h.assertTrue(modified != null && modified.parallels == 2, "Input availability must limit four segments to two parallels");
        feed(machine, new ItemStack(GFContent.item("dough"), 8));
        h.assertTrue(machine.fullModifyRecipe(recipe).parallels == 4, "Four complete segments must permit four parallels");
        for (var part : machine.getParts()) if (part instanceof ItemBusPartMachine bus && bus.getInventory().getHandlerIO() == com.gregtechceu.gtceu.api.capability.recipe.IO.OUT) {
            for (int slot = 0; slot < bus.getInventory().getSlots(); slot++) bus.getInventory().setStackInSlot(slot, new ItemStack(Items.COBBLESTONE, 64));
        }
        h.assertTrue(machine.fullModifyRecipe(recipe) == null, "Full outputs must block parallel processing");
        machine.onStructureInvalid();

        var low = build(h, false, 1);
        h.setBlock(ORIGIN.offset(1, 0, 1), GTMachines.ENERGY_INPUT_HATCH[GTValues.ULV].getBlock());
        h.assertTrue(low.checkPatternWithLock(), "ULV test structure should form but remain gated");
        low.onStructureFormed();
        feed(low, new ItemStack(GFContent.item("dough")));
        h.assertTrue(low.fullModifyRecipe(recipe) == null, "Low recipe EUt must not bypass the LV gate");
        low.onStructureInvalid();
        h.setBlock(ORIGIN.offset(0,2,0), GTMachines.ENERGY_INPUT_HATCH[GTValues.ULV].getBlock());
        h.assertTrue(low.checkPatternWithLock(), "Two ULV hatches should form");
        low.onStructureFormed();
        h.assertTrue(low.getMaxVoltage() >= GTValues.V[GTValues.LV], "Test must exercise GT's dual-hatch search-voltage boost");
        h.assertTrue(low.fullModifyRecipe(recipe) == null, "Two ULV hatches must not bypass actual LV voltage requirements");
        h.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 180)
    public static void rollingRunsOnActualServerTicks(GameTestHelper h) {
        var machine = form(h, false, 4);
        feed(machine, new ItemStack(GFContent.item("dough"), 4));
        h.onEachTick(() -> charge(machine));
        h.succeedWhen(() -> {
            h.assertTrue(outputCount(machine, GFContent.item("noodles")) == 8, "Four rolling segments must yield eight noodles");
            h.assertTrue(inputCount(machine, GFContent.item("dough")) == 0, "Dough was not consumed");
        });
    }

    @GameTest(template = "empty")
    public static void boilingConsumesFluidAndBowlsAndReturnsFood(GameTestHelper h) {
        var machine = form(h, true, 2);
        var recipe = recipe(h, "cooking_workshop/tomato-egg-noodles-refined");
        feed(machine, new ItemStack(GFContent.item("noodles"), 2), new ItemStack(GFContent.item("tomato"), 2),
                new ItemStack(Items.EGG, 2), new ItemStack(GFContent.item("scallion"), 2));
        water(machine, 1000);
        charge(machine);
        h.assertTrue(!machine.getRecipeLogic().checkMatchedRecipeAvailable(recipe), "Cooking without bowls must fail");
        feed(machine, new ItemStack(GFContent.item("noodles"), 2), new ItemStack(GFContent.item("tomato"), 2),
                new ItemStack(Items.EGG, 2), new ItemStack(GFContent.item("scallion"), 2), new ItemStack(Items.BOWL, 4));
        drain(machine);
        h.assertTrue(fluidAmount(machine) == 0, "Test setup did not empty the water hatch");
        h.assertTrue(!machine.getRecipeLogic().checkMatchedRecipeAvailable(recipe), "Cooking without real fluid must fail");
        water(machine, 1000);
        h.assertTrue(machine.getRecipeLogic().checkMatchedRecipeAvailable(recipe), "Valid boiling recipe did not start");
        h.assertTrue(machine.getRecipeLogic().getLastRecipe().parallels == 2, "Two boiling segments must run two batches");
        h.assertTrue(inputCount(machine, Items.BOWL) == 0 && fluidAmount(machine) == 0, "Bowl/water costs must scale with parallel count");
        long used = 0;
        for (int tick = 0; tick < recipe.duration + 1; tick++) {
            charge(machine);
            long before = storedEnergy(machine);
            machine.getRecipeLogic().serverTick();
            used += before - storedEnergy(machine);
        }
        h.assertTrue(used == (long) recipe.duration * 16, "Boiling must consume 16 EU/t for two parallels, got " + used);
        h.assertTrue(outputCount(machine, GFContent.item("tomato-egg-noodles-refined")) == 4, "Two batches must produce four individually stacked meals");
        var player = h.makeMockSurvivalPlayer();
        player.getFoodData().setFoodLevel(10);
        var meal = new ItemStack(GFContent.item("tomato-egg-noodles-refined"));
        var bowl = meal.finishUsingItem(h.getLevel(), player);
        h.assertTrue(bowl.is(Items.BOWL) && player.getFoodData().getFoodLevel() == 17, "Refined meal must feed player and return its bowl");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void machineRecipesAndUpstreamFlourBridgeExist(GameTestHelper h) {
        for (String id : new String[]{"controller/prep_workshop", "controller/cooking_workshop",
                "packer/flour_from_wheat_dust", "mixer/dough", "prep_workshop/rolled_noodles",
                "cooking_workshop/tomato-egg-noodles-refined"}) {
            h.assertTrue(h.getLevel().getRecipeManager().byKey(GregFoodExpansion.id(id)).isPresent(), "Missing recipe " + id);
        }
        var wheat = new ItemStack(Items.WHEAT);
        h.assertTrue(h.getLevel().getRecipeManager().getAllRecipesFor(GTRecipeTypes.MACERATOR_RECIPES).stream().anyMatch(r ->
                r.getInputContents(ItemRecipeCapability.CAP).stream().anyMatch(c -> ItemRecipeCapability.CAP.of(c.content).test(wheat))
                && r.getOutputContents(ItemRecipeCapability.CAP).stream().anyMatch(c ->
                        ItemRecipeCapability.CAP.of(c.content).test(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                                com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMaterials.Wheat)))),
                "The upstream wheat-to-dust route must remain available");
        h.succeed();
    }

    @GameTest(template = "empty")
    public static void rebuiltSegmentsRecountAndInterrupt(GameTestHelper h) {
        var machine = form(h, false, 1);
        machine.onStructureInvalid();
        // 原封底层(z=3)补上模块即成为新加工层:封底 ≡ 加工层去掉模块,固定件本就同位。
        h.setBlock(ORIGIN.offset(1, 1, 3), WorkshopPatterns.moduleBlock(false));
        // 新封底层(z=4):钢机壳 + 同位固定件。
        for (int y=0;y<3;y++) for (int x=0;x<3;x++) h.setBlock(ORIGIN.offset(x,y,4), GTBlocks.CASING_STEEL_SOLID.get());
        h.setBlock(ORIGIN.offset(1,0,4), WorkshopPatterns.fixtureBlock(false));
        h.assertTrue(machine.checkPatternWithLock(), "Expanded structure should form");
        machine.onStructureFormed();
        h.assertTrue(machine.segments() == 2, "Expansion must update this controller's segment count");
        feed(machine, new ItemStack(GFContent.item("dough"), 2));
        charge(machine);
        h.assertTrue(machine.getRecipeLogic().checkMatchedRecipeAvailable(recipe(h, "prep_workshop/rolled_noodles")), "Expanded recipe should start");
        machine.getRecipeLogic().serverTick();
        h.setBlock(ORIGIN.offset(1,1,2), Blocks.AIR);
        h.assertTrue(!machine.checkPatternWithLock(), "Broken segment must invalidate the structure");
        machine.onStructureInvalid();
        h.assertTrue(machine.segments() == 0 && machine.getRecipeLogic().getLastRecipe() == null,
                "Invalidation must clear segment capacity and in-flight recipe");
        h.setBlock(ORIGIN.offset(1,1,2), GTBlocks.CASING_STEEL_SOLID.get());
        h.assertTrue(machine.checkPatternWithLock(), "Restored base cap should form without segments");
        machine.onStructureFormed();
        h.assertTrue(machine.segments() == 0, "Detached structure behind a cap must not count as modules");
        h.succeed();
    }

    private static WorkshopMachine build(GameTestHelper h, boolean cooking, int segments) {
        // Remove previous test structure before rebuilding in the same template.
        for (int z = 0; z < 8; z++) for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) h.setBlock(ORIGIN.offset(x,y,z), Blocks.AIR);
        MultiblockMachineDefinition definition = cooking ? GFMachines.COOKING_WORKSHOP : GFMachines.PREP_WORKSHOP;
        var blocks = WorkshopPatterns.shape(definition, cooking, segments).getBlocks();
        for (int x = 0; x < blocks.length; x++) for (int y = 0; y < blocks[x].length; y++) for (int z = 0; z < blocks[x][y].length; z++)
            h.setBlock(ORIGIN.offset(x,y,z), blocks[x][y][z].getBlockState());
        return (WorkshopMachine) MetaMachine.getMachine(h.getLevel(), h.absolutePos(ORIGIN.offset(1,1,0)));
    }

    private static WorkshopMachine form(GameTestHelper h, boolean cooking, int segments) {
        var machine = build(h, cooking, segments);
        h.assertTrue(machine.checkPatternWithLock(), "Workshop structure does not match");
        machine.onStructureFormed();
        return machine;
    }

    private static GTRecipe recipe(GameTestHelper h, String path) {
        return (GTRecipe) h.getLevel().getRecipeManager().byKey(GregFoodExpansion.id(path)).orElseThrow();
    }
    private static java.util.List<ItemBusPartMachine> buses(WorkshopMachine machine, boolean input) {
        return machine.getParts().stream().filter(ItemBusPartMachine.class::isInstance).map(ItemBusPartMachine.class::cast)
                .filter(bus -> bus.getInventory().getHandlerIO() == (input ? com.gregtechceu.gtceu.api.capability.recipe.IO.IN : com.gregtechceu.gtceu.api.capability.recipe.IO.OUT)).toList();
    }
    private static void feed(WorkshopMachine machine, ItemStack... stacks) {
        int index = 0;
        for (var bus : buses(machine, true)) for (int slot = 0; slot < bus.getInventory().getSlots(); slot++) {
            bus.getInventory().setStackInSlot(slot, index < stacks.length ? stacks[index++].copy() : ItemStack.EMPTY);
        }
        if (index != stacks.length) throw new IllegalStateException("Insufficient test input slots");
    }
    private static int inputCount(WorkshopMachine m, Item item) { return count(m, item, true); }
    private static int outputCount(WorkshopMachine m, Item item) { return count(m, item, false); }
    private static int count(WorkshopMachine m, Item item, boolean input) {
        int total = 0;
        for (var bus : buses(m, input)) for (int i=0;i<bus.getInventory().getSlots();i++) {
            var stack = bus.getInventory().getStackInSlot(i);
            if (stack.is(item)) total += stack.getCount();
        }
        return total;
    }
    private static void charge(WorkshopMachine m) {
        for (var p : m.getParts()) if (p instanceof EnergyHatchPartMachine e) e.energyContainer.addEnergy(e.energyContainer.getEnergyCapacity());
    }
    private static long storedEnergy(WorkshopMachine m) {
        return m.getParts().stream().filter(EnergyHatchPartMachine.class::isInstance).map(EnergyHatchPartMachine.class::cast)
                .mapToLong(e -> e.energyContainer.getEnergyStored()).sum();
    }
    private static void water(WorkshopMachine m, int amount) {
        for (var p : m.getParts()) if (p instanceof FluidHatchPartMachine f) { f.tank.fill(GTMaterials.Water.getFluid(amount), FluidAction.EXECUTE); return; }
    }
    private static void drain(WorkshopMachine m) {
        for (var p : m.getParts()) if (p instanceof FluidHatchPartMachine f) f.tank.drainInternal(Integer.MAX_VALUE, FluidAction.EXECUTE);
    }
    private static int fluidAmount(WorkshopMachine m) {
        return m.getParts().stream().filter(FluidHatchPartMachine.class::isInstance).map(FluidHatchPartMachine.class::cast)
                .mapToInt(f -> f.tank.getFluidInTank(0).getAmount()).sum();
    }
}
