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

    private String pick(BilingualName name) {
        String en = name != null && name.en() != null ? name.en() : "";
        if ("zh_cn".equals(locale)) {
            return name != null && name.zh() != null ? name.zh() : en;
        }
        return "en_ud".equals(locale) ? EnglishUpsideDown.flip(en) : en;
    }
}
