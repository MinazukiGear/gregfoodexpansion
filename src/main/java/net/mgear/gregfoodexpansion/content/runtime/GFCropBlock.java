package net.mgear.gregfoodexpansion.content.runtime;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Wild and cultivated plants share a block; player-planted seeds always require farmland. */
public final class GFCropBlock extends CropBlock {
    public static final BooleanProperty WILD = BooleanProperty.create("wild");
    private final Supplier<Item> seed;
    public GFCropBlock(Properties properties, Supplier<Item> seed) {
        super(properties);
        this.seed = seed;
        registerDefaultState(defaultBlockState().setValue(WILD, false));
    }
    @Override protected ItemLike getBaseSeedId() { return seed.get(); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WILD);
    }
    @Override public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // Surface wild plants use the soil rule, like native bushes; placement cannot depend on initialized lighting.
        return state.getValue(WILD)
                ? level.getBlockState(pos.below()).is(BlockTags.DIRT)
                : super.canSurvive(state, level, pos);
    }
}
