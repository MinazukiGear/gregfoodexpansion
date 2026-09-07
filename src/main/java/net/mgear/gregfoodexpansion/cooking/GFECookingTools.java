package net.mgear.gregfoodexpansion.cooking;

import java.util.List;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.prep.GFEPrepToolItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 手工烹饪工具组(universal-cooker.md §7):工作台 + 工具(耐久消耗,不消耗本体),
 * LV 之前的烹饪兜底,规模刻意克制——进 LV 后由通用烹饪机全面接管。
 * 与机器四模式的对应:厨刀=刀工类 / 炒锅=炒煮一锅两用 / 蒸笼=蒸;炸无手工路径(决议)。
 */
public final class GFECookingTools {
    private GFECookingTools() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GregFoodExpansion.MOD_ID);

    /** 刀工类基础菜(凉拌/拼盘)。 */
    public static final RegistryObject<Item> KITCHEN_KNIFE = tool("kitchen_knife", 200);
    /** 炒、煮一锅两用。 */
    public static final RegistryObject<Item> WOK = tool("wok", 96);
    /** 蒸品/面点。 */
    public static final RegistryObject<Item> STEAMER = tool("steamer", 96);

    public static final List<RegistryObject<Item>> ALL = List.of(KITCHEN_KNIFE, WOK, STEAMER);

    private static RegistryObject<Item> tool(String name, int durability) {
        return ITEMS.register(name,
                () -> new GFEPrepToolItem(new Item.Properties().durability(durability)));
    }
}
