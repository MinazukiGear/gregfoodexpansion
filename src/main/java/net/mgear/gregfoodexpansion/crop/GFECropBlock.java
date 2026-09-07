package net.mgear.gregfoodexpansion.crop;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 统一作物方块(crop-system-foundation.md §3):vanilla 八档生长、仅可种于耕地、
 * 支持骨粉;基础生长概率沿用 vanilla {@link CropBlock#getGrowthSpeed}(耕地含水量、
 * 同作物邻格减产等原版因素全部保留),环境修正系数只作用其上——温和修正设计:
 * 参数不匹配只减速、不枯死(§3.3 草案数值,runClient 平衡测试后定案)。
 */
public class GFECropBlock extends CropBlock {
    private final CropEnvSpec spec;
    private final int tint;
    private final Supplier<Item> seed;

    public GFECropBlock(Properties properties, CropEnvSpec spec, int tint, Supplier<Item> seed) {
        super(properties);
        this.spec = spec;
        this.tint = tint;
        this.seed = seed;
    }

    public CropEnvSpec envSpec() {
        return spec;
    }

    /** 灰度生长模板的运行时染色基准色,与 tools/textures/generate_crop_textures.py 的 TINTS 表一致。 */
    public int tint() {
        return tint;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seed.get();
    }

    // 光照:存活判定不变(vanilla canSurvive ≥8 或见天);生长门限按 lightMin(默认 9)。
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getRawBrightness(pos, 0) < this.spec.lightMin()) {
            return;
        }
        int age = this.getAge(state);
        if (age >= this.getMaxAge()) {
            return;
        }
        // vanilla 基础速度下限 0.5(邻格减产后),乘最低修正 0.35 仍为正,无除零风险。
        float speed = getGrowthSpeed(this, level, pos) * growthModifier(level, pos);
        if (random.nextInt((int) (25.0F / speed) + 1) == 0) {
            level.setBlock(pos, this.getStateForAge(age + 1), 2);
        }
    }

    // 单参数越界 ×0.6,两项及以上 ×0.35;光照低于下限已在上方门限拦截。
    private float growthModifier(ServerLevel level, BlockPos pos) {
        int violations = 0;
        float temperature = level.getBiome(pos).value().getBaseTemperature();
        if (temperature < this.spec.tempMin() || temperature > this.spec.tempMax()) {
            violations++;
        }
        BlockState below = level.getBlockState(pos.below());
        if (below.getBlock() instanceof FarmBlock
                && below.getValue(FarmBlock.MOISTURE) < this.spec.hydrationMin()) {
            violations++;
        }
        return violations == 0 ? 1.0F : violations == 1 ? 0.6F : 0.35F;
    }
}
