package net.mgear.gregfoodexpansion;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.mgear.gregfoodexpansion.data.GFEAlcoholRecipes;
import net.mgear.gregfoodexpansion.data.GFECookingRecipes;
import net.mgear.gregfoodexpansion.data.GFEFermentingRecipes;
import net.mgear.gregfoodexpansion.data.GFEOilRefiningRecipes;
import net.mgear.gregfoodexpansion.data.GFEPrepRecipes;
import net.mgear.gregfoodexpansion.data.GFESoybeanRecipes;
import net.mgear.gregfoodexpansion.data.GFEStarchRecipes;
import net.mgear.gregfoodexpansion.data.GFELang;
import net.mgear.gregfoodexpansion.registry.GFERegistration;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

@GTAddon
public final class GregFoodExpansionAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GFERegistration.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        GFELang.init();
    }

    // 禁用 GTCEu 死面绕过酵母闸门的原版产物配方(2026-09-08 定案):
    // dough_to_bread 三途径(熔炉/营火/烟熏器)+ 南瓜派两路(手工 shapeless + 成型机)
    // + 曲奇两路(shapeless cookie_from_dough + 成型机 cookie)+ 蛋糕两路(shaped cake_from_dough
    // + shaped cake)。GT 自带的曲奇/蛋糕"原版替代配方"本身也吃 #forge:doughs 标签,不关则闸门漏风。
    // GTRecipes.recipeAddition 对所有经其注入的配方按 RECIPE_FILTERS 过滤,直接跳过生成。
    // 注:flour_to_dough(死面本体来源)保留,死面菜肴(葱油饼等)与蛋糕走本模组配方。
    @Override
    public void removeRecipes(Consumer<ResourceLocation> filters) {
        filters.accept(GTCEu.id("smelting/dough_to_bread"));
        filters.accept(GTCEu.id("campfire/dough_to_bread"));
        filters.accept(GTCEu.id("smoking/dough_to_bread"));
        filters.accept(GTCEu.id("shapeless/pumpkin_pie_from_dough"));
        filters.accept(GTCEu.id("shaped/pumpkin_pie_from_dough"));
        filters.accept(GTCEu.id("pumpkin_pie"));
        filters.accept(GTCEu.id("shapeless/cookie_from_dough"));
        filters.accept(GTCEu.id("cookie"));
        filters.accept(GTCEu.id("shapeless/cookie"));
        filters.accept(GTCEu.id("shaped/cake_from_dough"));
        filters.accept(GTCEu.id("shaped/cake"));
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GFEPrepRecipes.init(provider);
        GFECookingRecipes.init(provider);
        GFEOilRefiningRecipes.init(provider);
        GFESoybeanRecipes.init(provider);
        GFEStarchRecipes.init(provider);
        GFEFermentingRecipes.init(provider);
        GFEAlcoholRecipes.init(provider);
    }

    @Override
    public String addonModId() {
        return GregFoodExpansion.MOD_ID;
    }
}
