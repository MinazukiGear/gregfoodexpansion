package net.mgear.gregfoodexpansion.gains;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

/**
 * 增益数值结算管线(dishes-and-gains.md §2.1 已定案基准)。
 *
 * 最终时长 = 基准时长 × 修正项逐项连乘,取 min(总封顶,默认 20 min):
 * - SoL 多样性 ×1.0–1.5(线性插值):只乘时长不乘强度,杜绝"刷多样性叠强度";
 * - 味精 ×1.2 / 冻干 ×0.5 / M4 育种含量:对应系统未实现,接入时在此登记为
 *   {@link UnaryOperator} 修正项,顺序无关(全乘法);
 * - 实现期微调幅度 ±20% 需记录,不允许结构性重定。
 *
 * 读取时机:进食事件内实时计算,不持久缓存(m0-content-audit.md §5.4-4,
 * resetOnDeath=true 时自动回落,登录/切维度无需重算钩子)。
 */
public final class GFEGainPipeline {
    private GFEGainPipeline() {}

    /** SoL 多样性乘数(仅时长):未装 SoL / 总开关关闭 / 非玩家进食 → 1.0。 */
    public static float diversityMultiplier(LivingEntity entity) {
        if (!(entity instanceof net.minecraft.world.entity.player.Player player)) {
            return 1.0F;
        }
        if (!GFEGainConfigs.CARROT_SYNERGY_ENABLED.get()) {
            return 1.0F;
        }
        if (!GFESolBridge.available()) {
            return 1.0F;
        }
        return GFESolBridge.diversityMultiplier(player);
    }

    /** 单效果最终时长:基准 × 乘数连乘,总封顶;瞬时效果(≤0)原样返回。 */
    public static int finalDurationTicks(int baselineTicks, float multiplier) {
        if (baselineTicks <= 0) {
            return baselineTicks;
        }
        long capMinutes = GFEGainConfigs.GAIN_CAP_MINUTES.get();
        long cap = capMinutes * 60L * 20L;
        long scaled = Math.round(baselineTicks * (double) multiplier);
        return (int) Math.min(scaled, cap);
    }

    /**
     * 进食后调用(super.finishUsingItem 已按基准时长应用过效果):把本食品的
     * 全部效果按乘后时长重新应用。依赖 MobEffectInstance#update 的合并语义——
     * 同效果同强度时新实例时长更长则升级时长,强度永不变化(§2.1"只乘时长")。
     * 瞬时效果(饱和)时长无意义且重应用会二次结算,跳过。
     */
    public static void applyDiversityBoost(LivingEntity entity, ItemStack stack) {
        if (entity.level().isClientSide) {
            return;
        }
        float mult = diversityMultiplier(entity);
        if (mult <= 1.0F) {
            return;
        }
        FoodProperties props = stack.getFoodProperties(entity);
        if (props == null) {
            return;
        }
        for (Pair<MobEffectInstance, Float> pair : props.getEffects()) {
            MobEffectInstance base = pair.getFirst();
            if (base == null || base.getEffect().isInstantenous()) {
                continue;
            }
            entity.addEffect(new MobEffectInstance(base.getEffect(),
                    finalDurationTicks(base.getDuration(), mult),
                    base.getAmplifier(), base.isAmbient(), base.isVisible()));
        }
    }
}
