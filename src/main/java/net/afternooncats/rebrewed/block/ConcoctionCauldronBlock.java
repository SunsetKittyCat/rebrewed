package net.afternooncats.rebrewed.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class ConcoctionCauldronBlock extends BlockWithEntity {
    private static final VoxelShape RAYCAST_SHAPE = Block.createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
            VoxelShapes.fullCube(),
            VoxelShapes.union(
                    Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                    Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                    Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                    RAYCAST_SHAPE
            ),
            BooleanBiFunction.ONLY_FIRST
    );

    @Override
    protected MapCodec<? extends ConcoctionCauldronBlock> getCodec() {
        return createCodec(ConcoctionCauldronBlock::new);
    }

    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 3;
    public static final IntProperty LEVEL = IntProperty.of("level", MIN_LEVEL, MAX_LEVEL);

    public ConcoctionCauldronBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConcoctionCauldronBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(Items.CAULDRON);
    }
}