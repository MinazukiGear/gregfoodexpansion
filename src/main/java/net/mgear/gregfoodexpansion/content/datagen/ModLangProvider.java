package net.mgear.gregfoodexpansion.content.datagen;

import net.minecraftforge.common.data.LanguageProvider;
import net.minecraft.data.PackOutput;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.mgear.gregfoodexpansion.content.ContentTables;
import net.mgear.gregfoodexpansion.content.ContentTypes.AnimalEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.BaseIngredientEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.BilingualName;
import net.mgear.gregfoodexpansion.content.ContentTypes.CropEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.FlavorEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.MachineEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.MatrixTable;
import net.mgear.gregfoodexpansion.content.ContentTypes.RegistryTable;
import net.mgear.gregfoodexpansion.content.lang.EnglishUpsideDown;

/**
 * 三语语言生成(zh_cn/en_us/en_ud,content-pipeline.md §3"三语零差集")。
 * 条目全部来自内容表,不手写语言文件;en_ud = en_us 逐字符颠倒翻转(原版内置语言)。
 * 物品键约定:作物主产物 {@code item.<modid>.<cropId>};基料/味型/动物源产物直接用其 id;
 * 菜肴(矩阵行与登记行,每菜独立物品,gains-nutrition.md §5 SoL 契约)同样 {@code <id>}。
 */
public final class ModLangProvider extends LanguageProvider {
    private final ContentTables tables;
    private final String locale;

    public static ModLangProvider chinese(PackOutput output, ContentTables tables) {
        return new ModLangProvider(output, tables, "zh_cn");
    }

    public static ModLangProvider english(PackOutput output, ContentTables tables) {
        return new ModLangProvider(output, tables, "en_us");
    }

    public static ModLangProvider upsideDownEnglish(PackOutput output, ContentTables tables) {
        return new ModLangProvider(output, tables, "en_ud");
    }

    private ModLangProvider(PackOutput output, ContentTables tables, String locale) {
        super(output, GregFoodExpansion.MOD_ID, locale);
        this.tables = tables;
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.gregfoodexpansion", pick(new BilingualName("格雷食品扩展", "Greg Food Expansion")));
        text("block.gregfoodexpansion.clay_pot", "陶罐", "Clay Pot");
        text("item.gregfoodexpansion.unfired_clay_pot", "未烧制陶罐", "Unfired Clay Pot");
        text("item.gregfoodexpansion.water_bowl", "一碗水", "Bowl of Water");
        text("item.gregfoodexpansion.mortar", "研钵", "Mortar");
        text("item.gregfoodexpansion.rolling_pin", "擀面杖", "Rolling Pin");
        text("item.gregfoodexpansion.kitchen_knife", "燧石菜刀", "Flint Kitchen Knife");
        text("gregfoodexpansion.food.pending", "菜谱待开放", "Recipe not yet available");
        text("gregfoodexpansion.pot.ready", "%s · 剩余 %s 份，使用空碗盛取", "%s: %s servings left. Use an empty bowl.");
        text("gregfoodexpansion.pot.status", "%s | 水 %s/4 碗 | 加热 %s 秒", "%s | Water %s/4 bowls | Heated %s seconds");
        text("gregfoodexpansion.pot.help", "置于点燃的篝火上；右键逐个投料、加水；潜行空手取回食材", "Place above a lit campfire. Use ingredients and water bowls; sneak with an empty hand to retrieve ingredients.");
        text("gregfoodexpansion.pot.recipe", "番茄鸡蛋面：面条 + 番茄 + 鸡蛋 + 大葱，加 2 碗水，煮 60 秒；空碗取餐", "Tomato & egg noodles: noodles + tomato + egg + scallion, 2 bowls of water, 60 seconds. Serve with empty bowls.");
        for (var crop : tables.crops) {
            if (tables.gameplay.cultivation().contains(crop.id())) {
                text("item.gregfoodexpansion." + crop.id() + "_seeds", crop.name().zh() + "种子", crop.name().en() + " Seeds");
                text("block.gregfoodexpansion." + crop.id() + "_crop", crop.name().zh(), crop.name().en());
            }
        }
        text("gregfoodexpansion.module.rolling", "压延段", "Rolling segment");
        text("gregfoodexpansion.module.boiling", "煮段", "Boiling segment");
        text("gregfoodexpansion.workshop.capacity", "%s：%s 段，段数为并行上限", "%s: %s segments; one parallel per segment");
        text("gregfoodexpansion.workshop.missing", "缺少%s：向后扩建完整 3×3 结构段", "Missing %s: extend the rear with a complete 3x3 segment");
        text("gregfoodexpansion.workshop.requires", "需要结构：%s；最低 LV 供能", "Required structure: %s; minimum LV power");
        text("gregfoodexpansion.workshop.voltage", "需要 LV 或更高电压的能源仓", "Requires an LV or higher voltage energy hatch");
        text("gregfoodexpansion.workshop.rolling_help", "后部追加 1–4 段；每段 8 钢机壳 + 中央钢齿轮箱，解锁压延并增加并行", "Add 1-4 rear segments: 8 solid steel casings around a steel gearbox per segment. Enables rolling and increases parallel capacity.");
        text("gregfoodexpansion.workshop.boiling_help", "后部追加 1–4 段；每段 8 钢机壳 + 中央青铜管道方块；需 2 输入总线与流体输入仓", "Add 1-4 rear segments: 8 solid steel casings around a bronze pipe casing. Requires 2 input buses and a fluid input hatch.");
        for (var dish : tables.gameplay.machineDishes()) {
            var source = tables.matrixTables.stream().flatMap(t -> t.rows().stream())
                    .filter(r -> r.id().equals(dish.source())).findFirst().orElseThrow();
            text("item.gregfoodexpansion." + dish.id(), "精制" + source.name().zh(), "Refined " + source.name().en());
        }
        for (MachineEntry machine : tables.machines) {
            add("block." + GregFoodExpansion.MOD_ID + "." + machine.id(), pick(machine.name()));
        }
        add("gregfoodexpansion.machine.skeleton_note", pick(new BilingualName(
                "骨架实装阶段:结构先行,配方随 M1' 内容批次开放",
                "Skeleton build: structure first; recipes arrive with the M1' content batches.")));
        for (CropEntry crop : tables.crops) {
            add("item." + GregFoodExpansion.MOD_ID + "." + crop.id(), pick(crop.name()));
        }
        for (BaseIngredientEntry base : tables.baseIngredients) {
            add("item." + GregFoodExpansion.MOD_ID + "." + base.id(), pick(base.name()));
        }
        for (FlavorEntry flavor : tables.flavors) {
            add("item." + GregFoodExpansion.MOD_ID + "." + flavor.id(), pick(flavor.name()));
        }
        for (AnimalEntry animal : tables.animals) {
            animal.products().forEach(p ->
                    add("item." + GregFoodExpansion.MOD_ID + "." + p.id(), pick(p.name())));
        }
        for (MatrixTable table : tables.matrixTables) {
            table.rows().forEach(row ->
                    add("item." + GregFoodExpansion.MOD_ID + "." + row.id(), pick(row.name())));
        }
        for (RegistryTable table : tables.registryTables) {
            table.rows().forEach(row ->
                    add("item." + GregFoodExpansion.MOD_ID + "." + row.id(), pick(row.name())));
        }
    }

    private void text(String key, String zh, String en) {
        add(key, pick(new BilingualName(zh, en)));
    }

    private String pick(BilingualName name) {
        String en = name != null && name.en() != null ? name.en() : "";
        if ("zh_cn".equals(locale)) {
            return name != null && name.zh() != null ? name.zh() : en;
        }
        return "en_ud".equals(locale) ? EnglishUpsideDown.flip(en) : en;
    }
}
