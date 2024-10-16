package net.afternooncats.rebrewed.fluid;

import net.minecraft.block.Block;

public class VanillaCauldronFluid extends AbstractCauldronFluid{
    private final Block vanillaCauldron;

    public VanillaCauldronFluid(int color, int defTempType, boolean defWet, Block vanillaCauldron) {
        super(color, defTempType, defWet);
        this.vanillaCauldron = vanillaCauldron;
    }

    public Block getVanillaCauldron() {
        return vanillaCauldron;
    }
}
