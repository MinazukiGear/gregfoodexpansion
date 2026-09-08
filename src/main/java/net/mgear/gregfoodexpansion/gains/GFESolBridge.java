package net.mgear.gregfoodexpansion.gains;

import com.cazsius.solcarrot.SOLCarrotConfig;
import com.cazsius.solcarrot.api.FoodCapability;
import com.cazsius.solcarrot.api.SOLCarrotAPI;
import net.minecraft.world.entity.player.Player;

/**
 * SoL Carrot Edition 胡萝卜版桥接(m0-content-audit.md §5.3/§5.4)。
 *
 * 类隔离:本类是全模组唯一允许引用 solcarrot 类的地方;调用点必须先用
 * {@link #available()}(ModList 检查)门控,JVM 惰性解析保证未安装 SoL 时
 * 本类永远不会被加载。
 *
 * 口径说明(v1):getEatenFoodCount() 是未过滤口径(SoL 作者留有 TODO),
 * 严格过滤所需的食物列表访问在非 stable 的 FoodList 类中,v1 不引用;
 * 本模组食品营养值均 ≥1 且不在默认黑名单,两口径在本场景下等价,
 * 详见 m0-content-audit.md §5.4-5 的 strictFiltering 决议回填。
 */
final class GFESolBridge {
    private GFESolBridge() {}

    /** 是否已安装 solcarrot(调用点门控,放行后才允许触碰本类其余方法)。 */
    static boolean available() {
        return net.minecraftforge.fml.ModList.get().isLoaded("solcarrot");
    }

    /**
     * 多样性乘数:1.0(0 种)→ 上限(SoL 最高里程碑种数)线性插值,
     * 锚点实时读 solcarrot serverconfig(milestones 改配置自动跟随,不硬编码)。
     */
    static float diversityMultiplier(Player player) {
        FoodCapability cap = SOLCarrotAPI.getFoodCapability(player);
        if (cap == null) {
            return 1.0F;
        }
        int anchor = SOLCarrotConfig.highestMilestone();
        if (anchor <= 0) {
            return 1.0F;
        }
        int count = cap.getEatenFoodCount();
        float max = GFEGainConfigs.CARROT_MAX_MULTIPLIER.get().floatValue();
        float t = Math.min(1.0F, (float) count / (float) anchor);
        return 1.0F + (max - 1.0F) * t;
    }
}
