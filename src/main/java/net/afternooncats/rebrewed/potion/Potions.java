package net.afternooncats.rebrewed.potion;

import net.afternooncats.rebrewed.Rebrewed;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class Potions {
    public static final RegistryEntry<Potion> COMPOSITE = register("composite", new Potion());

    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(Rebrewed.MOD_ID, name), potion);
    }

    public static void initialize() {

    }
}
