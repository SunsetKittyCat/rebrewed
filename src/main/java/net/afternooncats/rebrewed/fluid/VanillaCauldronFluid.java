package net.afternooncats.rebrewed.fluid;

import net.minecraft.block.Block;

public class VanillaCauldronFluid extends AbstractCauldronFluid{
    private final Block vanillaCauldron;

    public VanillaCauldronFluid(int color, FluidTemp tempType, boolean wet, Block vanillaCauldron) {
        super(color, tempType, wet);
        this.vanillaCauldron = vanillaCauldron;
    }

    public Block getVanillaCauldron() {
        return vanillaCauldron;
    }
}
