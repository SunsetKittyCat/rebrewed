package net.afternooncats.rebrewed.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.BlockRenderView;

public class ConcoctionCauldronBlockEntity extends BlockEntity {
    private int color = ColorHelper.Argb.getArgb(255, 0, 255);

    public ConcoctionCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.CONCOCTION_CAULDRON, pos, state);
    }

    @Environment(EnvType.CLIENT)
    public static int getColor(BlockRenderView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ConcoctionCauldronBlockEntity))
            return -1;

        return ((ConcoctionCauldronBlockEntity) blockEntity).color;
    }
}