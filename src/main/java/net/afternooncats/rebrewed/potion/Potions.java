package net.afternooncats.rebrewed.potion;

import net.afternooncats.rebrewed.Rebrewed;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class Potions {
    public static final RegistryEntry<Potion> COMPOSITE = register("composite", new Potion());
    public static final RegistryEntry<Potion> REJUVENATION = register("rejuvenation",
            new Potion("regeneration", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.REGENERATION, 400)
            })); //Ghast Tear
    //Insert axolotl cell Regeneration
    public static final RegistryEntry<Potion> POISON = register("poison",
            new Potion("poison", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.POISON, 600)
            })); //Poisonous Potato
    //Glistening Melon Healing is identical to vanilla, no need to add
    public static final RegistryEntry<Potion> DECAY = register("decay",
            new Potion("decay", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1),
                    new StatusEffectInstance(StatusEffects.WITHER, 20)
            })); //Withered Bone
    public static final RegistryEntry<Potion> WITHERING = register("withering",
            new Potion("withering", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.WITHER, 300)
            })); //Wither Rose
    public static final RegistryEntry<Potion> FLAME_COAT = register("flame_coat",
            new Potion("flame_coat", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1800)
            })); //Magma Cream
    //Insert Polar Bear Fur Warming
    public static final RegistryEntry<Potion> TURTLE_MASTER = register("turtle_master",
            new Potion("turtle_master", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 3),
                    new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 2)
            })); //Turtle Shell
    public static final RegistryEntry<Potion> BLAZING_STRENGTH = register("blazing_strength",
            new Potion("blazing_strength", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.STRENGTH, 1800)
                    //also flammability
            })); //Blaze Powder, maybe change to Blaze Rod
    public static final RegistryEntry<Potion> NAUSEA = register("nausea",
            new Potion("nausea", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.NAUSEA, 400)
            })); //Spider Eye
    public static final RegistryEntry<Potion> BLIND_EYE = register("blind_eye",
            new Potion("blind_eye", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.WEAKNESS, 900),
                    new StatusEffectInstance(StatusEffects.INVISIBILITY, 900)
            })); //Fermented Spider Eye, maybe change to str + blindness?
    public static final RegistryEntry<Potion> SUGAR_RUSH = register("sugar_rush",
            new Potion("sugar_rush", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.SPEED, 1200),
                    new StatusEffectInstance(StatusEffects.HASTE, 1200)
            })); //Sugar
    public static final RegistryEntry<Potion> RABBIT = register("rabbit",
            new Potion("rabbit", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.JUMP_BOOST, 1800),
                    new StatusEffectInstance(StatusEffects.LUCK, 1800)
            })); //Rabbit's Foot
    public static final RegistryEntry<Potion> EYESIGHT = register("eyesight",
            new Potion("eyesight", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1800)
            })); //Golden Carrot
    public static final RegistryEntry<Potion> INK = register("ink",
            new Potion("ink", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.BLINDNESS, 200)
            })); //Ink Sac
    public static final RegistryEntry<Potion> GLOWING_INK = register("glowing_ink",
            new Potion("glowing_ink", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.BLINDNESS, 200),
                    new StatusEffectInstance(StatusEffects.GLOWING, 600)
            })); //Glow Ink Sac
    public static final RegistryEntry<Potion> EXPANDING_GILLS = register("expanding_gills",
            new Potion("water_breathing", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.WATER_BREATHING, 1800)
                    //also maybe size change up
            })); //Pufferfish
    public static final RegistryEntry<Potion> GHOST = register("ghost",
            new Potion("ghost", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.SLOW_FALLING, 900),
                    new StatusEffectInstance(StatusEffects.INVISIBILITY, 600)
            })); //Phantom Membrane
    public static final RegistryEntry<Potion> BREEZY_FOOTING = register("breezy_footing",
            new Potion("breezy_footing", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.WIND_CHARGED, 1800),
                    new StatusEffectInstance(StatusEffects.SPEED, 1800, 2),
                    new StatusEffectInstance(StatusEffects.JUMP_BOOST, 1800, 2)
            })); //Wind Charge? Breeze Rod if BLAZING_STRENGTH changes to Blaze Rod
    public static final RegistryEntry<Potion> ENTANGLEMENT = register("entanglement",
            new Potion("entanglement", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.WEAVING, 1800),
                    new StatusEffectInstance(StatusEffects.SLOWNESS, 1800)
            })); //Cobweb
    public static final RegistryEntry<Potion> SLIME = register("slime",
            new Potion("oozing", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.OOZING, 1800)
                    //also sticky
            })); //Bucket of Slime
    public static final RegistryEntry<Potion> INFESTATION = register("infestation",
            new Potion("infested", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.INFESTED, 1800),
                    new StatusEffectInstance(StatusEffects.NIGHT_VISION, 3600)
            })); //Silver Eggs
    public static final RegistryEntry<Potion> BLINDING_FAITH = register("blinding_faith",
            new Potion("blinding_faith", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.BLINDNESS, 300)
                    //also warping
            })); //Ender Pearl
    //add Chorus Fruit Warping



    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(Rebrewed.MOD_ID, name), potion);
    }

    public static void initialize() {

    }
}
