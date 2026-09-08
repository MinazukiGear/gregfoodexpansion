package net.mgear.gregfoodexpansion.data;

import com.tterrag.registrate.providers.RegistrateItemModelProvider;

import net.mgear.gregfoodexpansion.alcohol.GFEAlcoholItems;
import net.mgear.gregfoodexpansion.cooking.GFECookingTools;
import net.mgear.gregfoodexpansion.cooking.GFEDishes;
import net.mgear.gregfoodexpansion.prep.GFEFormItems;
import net.mgear.gregfoodexpansion.prep.GFEPrepTools;
import net.mgear.gregfoodexpansion.registry.GFECropItems;
import net.mgear.gregfoodexpansion.soybean.GFESoybeanItems;

public final class GFEItemModels {
    private GFEItemModels() {}

    public static void init(RegistrateItemModelProvider provider) {
        // item/generated + layer0 = gregfoodexpansion:item/<注册名>,贴图路径已与注册名对齐。
        GFECropItems.ALL_SEEDS.forEach(item -> provider.basicItem(item.get()));
        GFECropItems.ALL_PRODUCTS.forEach(item -> provider.basicItem(item.get()));
        GFEFormItems.ALL.forEach(item -> provider.basicItem(item.get()));
        GFEPrepTools.ALL.forEach(item -> provider.basicItem(item.get()));
        GFEDishes.ALL.forEach(item -> provider.basicItem(item.get()));
        GFECookingTools.ALL.forEach(item -> provider.basicItem(item.get()));
        GFESoybeanItems.ALL.forEach(item -> provider.basicItem(item.get()));
        GFEAlcoholItems.ALL.forEach(item -> provider.basicItem(item.get()));
    }
}
