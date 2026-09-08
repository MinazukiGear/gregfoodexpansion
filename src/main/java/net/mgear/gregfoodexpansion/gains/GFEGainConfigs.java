package net.mgear.gregfoodexpansion.gains;

import net.mgear.gregfoodexpansion.GregFoodExpansion;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 增益结算管线配置(dishes-and-gains.md §2.1,m0-content-audit.md §5.4)。
 * COMMON 配置:进食时实时读取,服主可调;乘数档位本身实时锚定 SoL 的 milestones
 * serverconfig(SOLCarrotConfig.getMilestones()),此处只管乘数上限与封顶。
 */
public final class GFEGainConfigs {
    private GFEGainConfigs() {}

    public static final ForgeConfigSpec SPEC;
    /** SoL 多样性乘数总开关(关闭后管线退化为基准时长直通)。 */
    public static final ForgeConfigSpec.BooleanValue CARROT_SYNERGY_ENABLED;
    /** 多样性乘数上限(定案 ×1.5;实现期 ±20% 微调允许 1.2–1.8,放宽到 3.0 便于实测)。 */
    public static final ForgeConfigSpec.DoubleValue CARROT_MAX_MULTIPLIER;
    /** 修正项连乘后的单效果时长封顶(分钟),dishes-and-gains.md §2.1 定案 20 min。 */
    public static final ForgeConfigSpec.IntValue GAIN_CAP_MINUTES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("gainPipeline");
        CARROT_SYNERGY_ENABLED = builder
                .comment("SoL Carrot Edition diversity multiplier master switch",
                        "(modid solcarrot, soft dependency; disabled = baseline durations only)")
                .define("carrotSynergyEnabled", true);
        CARROT_MAX_MULTIPLIER = builder
                .comment("Diversity multiplier ceiling (design default 1.5, dishes-and-gains.md #2.1).",
                        "Multiplier interpolates linearly from 1.0 (0 kinds) to this ceiling",
                        "at SoL's highest milestone; anchor follows solcarrot's serverconfig in real time.")
                .defineInRange("carrotMaxMultiplier", 1.5, 1.0, 3.0);
        GAIN_CAP_MINUTES = builder
                .comment("Per-effect duration cap in minutes after all multipliers are applied",
                        "(design default 20 = safety fuse of the multiplicative pipeline).")
                .defineInRange("gainCapMinutes", 20, 1, 120);
        builder.pop();
        SPEC = builder.build();
    }

    public static void register() {
        net.minecraftforge.fml.ModLoadingContext.get()
                .registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, SPEC);
    }
}
