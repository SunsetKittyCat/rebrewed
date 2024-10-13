package net.afternooncats.rebrewed.fluid;

import net.afternooncats.rebrewed.Rebrewed;
import net.minecraft.block.Blocks;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class CauldronFluids {
    public static final RegistryKey<Registry<AbstractCauldronFluid>> CFLUID_KEY = RegistryKey.ofRegistry(Identifier.of(Rebrewed.MOD_ID, "cauldron_fluid"));
    public static final DefaultedRegistry<AbstractCauldronFluid> CAULDRON_FLUID = Registries.create(CFLUID_KEY, "empty", (registry) -> {
        return CauldronFluids.EMPTY_FLUID;
    });
    public static VanillaCauldronFluid EMPTY_FLUID = Registry.register(CAULDRON_FLUID, Identifier.of("minecraft", "empty"), new VanillaCauldronFluid(ColorHelper.Argb.getArgb(0, 0, 0), AbstractCauldronFluid.FluidTemp.NORMAL, false, Blocks.CAULDRON));
    public static VanillaCauldronFluid WATER_FLUID = Registry.register(CAULDRON_FLUID, Identifier.of("minecraft", "water"), new VanillaCauldronFluid(ColorHelper.Argb.getArgb(0, 0, 255), AbstractCauldronFluid.FluidTemp.NORMAL, true, Blocks.WATER_CAULDRON));
    public static VanillaCauldronFluid LAVA_FLUID = Registry.register(CAULDRON_FLUID, Identifier.of("minecraft", "lava"), new VanillaCauldronFluid(ColorHelper.Argb.getArgb(255, 63, 0), AbstractCauldronFluid.FluidTemp.HOT, false, Blocks.LAVA_CAULDRON));
    public static VanillaCauldronFluid POWDER_SNOW_FLUID = Registry.register(CAULDRON_FLUID, Identifier.of("minecraft", "powder_snow"), new VanillaCauldronFluid(ColorHelper.Argb.getArgb(240, 220, 255), AbstractCauldronFluid.FluidTemp.COLD, true, Blocks.POWDER_SNOW_CAULDRON));
    public static AbstractCauldronFluid BREWING_FLUID = Registry.register(CAULDRON_FLUID, Identifier.of(Rebrewed.MOD_ID, "brewing_fluid"), new AbstractCauldronFluid(ColorHelper.Argb.getArgb(31, 63, 255), AbstractCauldronFluid.FluidTemp.COLD, true));
    
    public static void registerCauldronFluids() {
        
    }
}
