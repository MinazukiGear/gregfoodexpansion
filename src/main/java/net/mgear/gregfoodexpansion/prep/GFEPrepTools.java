package net.mgear.gregfoodexpansion.prep;

import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mgear.gregfoodexpansion.GregFoodExpansion;

/**
 * 手工切配工具组(food-processor.md §7):工作台 + 工具(耐久消耗,不消耗本体),
 * 配方刻意少,LV 后由切配机接管。首版铁质起步;耐久数值为草案基准(§10-5)。
 */
public final class GFEPrepTools {
    private GFEPrepTools() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GregFoodExpansion.MOD_ID);

    /** 覆盖切片/切丝/切块/绞碎。 */
    public static final RegistryObject<Item> CLEAVER = tool("cleaver", 250);
    /** 覆盖剥皮。 */
    public static final RegistryObject<Item> PEELER = tool("peeler", 128);
    /** 覆盖研磨。 */
    public static final RegistryObject<Item> MORTAR_PESTLE = tool("mortar_pestle", 96);
    /** 覆盖压延。 */
    public static final RegistryObject<Item> ROLLING_PIN = tool("rolling_pin", 64);

    public static final List<RegistryObject<Item>> ALL = List.of(
            CLEAVER, PEELER, MORTAR_PESTLE, ROLLING_PIN);

    private static RegistryObject<Item> tool(String name, int durability) {
        return ITEMS.register(name,
                () -> new GFEPrepToolItem(new Item.Properties().durability(durability)));
    }
}
