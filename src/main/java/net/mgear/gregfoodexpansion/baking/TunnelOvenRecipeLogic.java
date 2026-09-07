package net.mgear.gregfoodexpansion.baking;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * 隧道式烤炉配方逻辑:仅接受 cooking 类型中带 `oven_temp` 数据的烤配方(电路 5),
 * 并按炉温档位门控——炉温未达配方要求档位时拒绝开工(提示预热中)。
 */
public class TunnelOvenRecipeLogic extends RecipeLogic {

    public TunnelOvenRecipeLogic(TunnelOvenMachine machine) {
        super(machine);
    }

    @Override
    public TunnelOvenMachine getMachine() {
        return (TunnelOvenMachine) super.getMachine();
    }

    /** 只搜索烤配方(配方数据带 oven_temp),c1-c4 烹饪配方由通用烹饪机承接。 */
    @Override
    public @NotNull Iterator<GTRecipe> searchRecipe() {
        return machine.getRecipeType().searchRecipe(machine, recipe -> recipe.data.getInt("oven_temp") > 0);
    }

    @Override
    protected ActionResult checkRecipe(GTRecipe recipe) {
        int required = recipe.data.getInt("oven_temp");
        if (getMachine().getCurrentTempTier() < required) {
            return ActionResult.fail(Component.translatable("gregfoodexpansion.multiblock.tunnel_oven.preheating"), null, IO.IN);
        }
        return super.checkRecipe(recipe);
    }
}
