package net.mgear.gregfoodexpansion;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.mgear.gregfoodexpansion.registry.GFERegistration;

/**
 * GTCEu 附属接入点。
 *
 * 高档模式策略(tier-map.md §2):本模组**不强制开启**高档模式——
 * requiresHighTier() 保持默认 false。默认环境内容实装至 UV 收口
 * (元素膳造厂为 UV 机器,标准档位可用);UHV–OpV/MAX 后续内容
 * (风味/质构/缓释配料、分子料理编译器、造粮机)在注册期检测
 * {@code GTCEuAPI.isHighTier()},仅高档环境注册。整合包通过
 * {@code machines.highTierContent=true} 开启高档后自动补全。
 */
@GTAddon
public final class GregFoodExpansionAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GFERegistration.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        // 内容注册随实装批次重建(见 docs/design/milestones.md)
    }

    @Override
    public String addonModId() {
        return GregFoodExpansion.MOD_ID;
    }
}
