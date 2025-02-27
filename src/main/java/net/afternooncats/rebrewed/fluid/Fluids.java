package net.afternooncats.rebrewed.fluid;

import net.afternooncats.rebrewed.Rebrewed;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Fluids {
    public static FlowableFluid BREWING_FLUID = (FlowableFluid) register("brewing_fluid", new BrewingFluid());

    public static Fluid register(String name, Fluid fluid) {
        Identifier id = Identifier.of(Rebrewed.MOD_ID, name);
        return Registry.register(Registries.FLUID, id, fluid);
    }

    public static void initialize() {}
}
