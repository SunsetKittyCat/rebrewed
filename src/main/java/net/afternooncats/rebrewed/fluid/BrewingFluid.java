package net.afternooncats.rebrewed.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class BrewingFluid extends WaterFluid {

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        super.beforeBreakingBlock(world, pos, state);
        world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX(), pos.getY(), pos.getZ(), 0.0, 1.0, 0.0);
    }

    @Override
    public int getLevel(FluidState state) {
        return 1;
    }

    @Override
    public boolean isStill(FluidState state) {
        return true;
    }
}
