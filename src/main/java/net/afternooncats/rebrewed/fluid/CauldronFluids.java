package net.afternooncats.rebrewed.fluid;

import net.minecraft.block.Blocks;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class CauldronFluids {
    public static final DefaultedRegistry<AbstractCauldronFluid> CAULDRON_FLUID = null;
    public static VanillaCauldronFluid WATER_FLUID = Registry.register(CAULDRON_FLUID, Identifier.of("minecraft", "water"), new VanillaCauldronFluid(ColorHelper.Argb.getArgb(0, 0, 255), AbstractCauldronFluid.FluidTemp.NORMAL, true, Blocks.WATER_CAULDRON));
    public static VanillaCauldronFluid LAVA_FLUID = Registry.register(CAULDRON_FLUID, Identifier.of("minecraft", "lava"), new VanillaCauldronFluid(ColorHelper.Argb.getArgb(255, 63, 0), AbstractCauldronFluid.FluidTemp.HOT, false, Blocks.LAVA_CAULDRON));
    public static VanillaCauldronFluid POWDER_SNOW_FLUID = Registry.register(CAULDRON_FLUID, Identifier.of("minecraft", "powder_snow"), new VanillaCauldronFluid(ColorHelper.Argb.getArgb(240, 220, 255), AbstractCauldronFluid.FluidTemp.COLD, true, Blocks.POWDER_SNOW_CAULDRON));
    
    public static void registerCauldronFluids() {
        
    }
}
