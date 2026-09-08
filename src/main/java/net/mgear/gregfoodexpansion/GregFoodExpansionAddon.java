package net.mgear.gregfoodexpansion;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

/**
 * GTCEu 附属接入点。
 *
 * requiresHighTier()=true 是顶档政策的实现契约(tier-map.md §2):
 * 本模组内容铺排到 OpV、MAX 收尾,依赖 GTCEu 高档模式提供的 UHV+ 外壳/仓室
 * 与 OpV 档机器支持;该声明会为本整合包环境全局开启高档模式
 * (等价于 machines.highTierContent=true),副作用(解禁 GT 的 UHV+ 外壳与仓室)
 * 需在 README 面向整合包作者明示。
 */
@GTAddon
public final class GregFoodExpansionAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GTRegistrate.create(GregFoodExpansion.MOD_ID);
    }

    @Override
    public void initializeAddon() {
        // 内容注册随实装批次重建(见 docs/design/milestones.md)
    }

    @Override
    public boolean requiresHighTier() {
        return true;
    }

    @Override
    public String addonModId() {
        return GregFoodExpansion.MOD_ID;
    }
}
