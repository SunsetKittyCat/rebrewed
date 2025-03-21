package net.afternooncats.rebrewed.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.potion.Potion;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.Optional;

public class FluidInstance {
    private FluidVariant fluid;

    public FluidInstance(Fluid fluid) {
        this(fluid, new ArrayList<>());
    }
    public FluidInstance(Fluid fluid, ArrayList<StatusEffectInstance> effects) {
        this.fluid = buildFluidVariant(fluid,
                fluid.getDefaultState().getBlockState().getBlock().getDefaultMapColor().color,
                effects);
    }
    public FluidInstance(FluidVariant fluid) {
        this.fluid = fluid;
    }

    public static FluidVariant buildFluidVariant(Fluid fluid, int color, ArrayList<StatusEffectInstance> effects) {
        return buildFluidVariant(fluid, net.minecraft.potion.Potions.WATER, color, effects);
    }
    public static FluidVariant buildFluidVariant(Fluid fluid, RegistryEntry<Potion> potion, int color, ArrayList<StatusEffectInstance> effects) {
        return buildFluidVariant(fluid, new PotionContentsComponent(
                Optional.of(potion),
                Optional.of(color),
                effects));
    }
    public static FluidVariant buildFluidVariant(Fluid fluid, PotionContentsComponent potion) {
        ComponentChanges.Builder comp = ComponentChanges.builder();
        comp.add(DataComponentTypes.POTION_CONTENTS, potion);
        return FluidVariant.of(fluid, comp.build());
    }

    public FluidVariant getFluidVar() {
        return this.fluid;
    }
    public Fluid getFluid() {
        return this.fluid.getFluid();
    } //maybe potentially add a setType but not rn
    public ComponentChanges getComponents() {
        return this.fluid.getComponents();
    }

    public int getColor() {
        return !this.getComponents().isEmpty() && this.getComponents().get(DataComponentTypes.POTION_CONTENTS).isPresent() ? this.getComponents().get(DataComponentTypes.POTION_CONTENTS).get().getColor() : this.getFluid().getDefaultState().getBlockState().getBlock().getDefaultMapColor().color;
    }
    public PotionContentsComponent getPotion() {
        return !this.getComponents().isEmpty() && this.getComponents().get(DataComponentTypes.POTION_CONTENTS).isPresent() ? this.getComponents().get(DataComponentTypes.POTION_CONTENTS).get() : new PotionContentsComponent(net.minecraft.potion.Potions.WATER);
    }
    public ArrayList<StatusEffectInstance> getEffects() {
        if (this.getComponents().isEmpty() || this.getComponents().get(DataComponentTypes.POTION_CONTENTS).isEmpty()) return new ArrayList<>();
        Iterable<StatusEffectInstance> statuses = this.getComponents().get(DataComponentTypes.POTION_CONTENTS).get().getEffects();
        ArrayList<StatusEffectInstance> effects = new ArrayList<>();
        statuses.forEach(effects::add);
        return effects;
    }

    public void setColor(int newColor) {
        this.fluid = buildFluidVariant(this.getFluid(), newColor, this.getEffects());
    }
    public void setPotion(PotionContentsComponent potion) {
        this.fluid = buildFluidVariant(this.getFluid(), potion);
    }
    public void setEffects(ArrayList<StatusEffectInstance> effects) {
        this.fluid = buildFluidVariant(this.getFluid(), this.getColor(), effects);
    }

    private void addColor(int newColor, double ratioAdded) {
        int a = (int) ((ColorHelper.Argb.getAlpha(this.getColor()) * (1-ratioAdded)) + (ColorHelper.Argb.getAlpha(newColor) * ratioAdded));
        int r = (int) ((ColorHelper.Argb.getRed(this.getColor()) * (1-ratioAdded)) + (ColorHelper.Argb.getRed(newColor) * ratioAdded));
        int g = (int) ((ColorHelper.Argb.getGreen(this.getColor()) * (1-ratioAdded)) + (ColorHelper.Argb.getGreen(newColor) * ratioAdded));
        int b = (int) ((ColorHelper.Argb.getBlue(this.getColor()) * (1-ratioAdded)) + (ColorHelper.Argb.getBlue(newColor) * ratioAdded));

        this.setColor(ColorHelper.Argb.getArgb(a, r, g, b));
    }

    // TODO: make potions retain their potion type if potion added is of the same type
    public void addPotion(PotionContentsComponent newPotion, double ratioAdded) {
        ArrayList<StatusEffectInstance> newEffects = new ArrayList<>();
        PotionContentsComponent currentPotion = this.getPotion();

        newPotion.forEachEffect(potionEffect -> {
            boolean inCauldron = false;

            for (StatusEffectInstance cauldronEffect : currentPotion.getEffects()) {
                if (potionEffect.getEffectType() == cauldronEffect.getEffectType())
                {
                    int amp = Math.max(cauldronEffect.getAmplifier(), potionEffect.getAmplifier());
                    int duration = (int) ((cauldronEffect.getDuration() * Math.pow(1d/4, (amp - cauldronEffect.getAmplifier()))) + (potionEffect.getDuration() * Math.pow(1d/4, (amp - potionEffect.getAmplifier()))));
                    newEffects.add(new StatusEffectInstance(cauldronEffect.getEffectType(), duration, amp, cauldronEffect.isAmbient(), cauldronEffect.shouldShowParticles(), cauldronEffect.shouldShowIcon()));
                    inCauldron = true;
                    break;
                }
            }

            if (!inCauldron) newEffects.add(potionEffect);
        });

        currentPotion.forEachEffect(cauldronEffect -> {
            boolean inPotion = false;

            for (StatusEffectInstance potionEffect : newPotion.getEffects()) {
                if (cauldronEffect.getEffectType() == potionEffect.getEffectType())
                {
                    inPotion = true;
                    break;
                }
            }

            if (!inPotion)
                newEffects.add(cauldronEffect);
        });

        this.setEffects(newEffects);
        this.addColor(newPotion.getColor(), ratioAdded);
    }

    public PotionContentsComponent removePotion(double ratioTaken) {
        ArrayList<StatusEffectInstance> outputEffects = new ArrayList<>();
        ArrayList<StatusEffectInstance> cauldronEffects = new ArrayList<>();

        // TODO: Create solution for infinitely-diluting potions (minimum duration is 1)
        // holy water problem
        this.getPotion().forEachEffect(effect -> {
            //add effect to potion list
            outputEffects.add(new StatusEffectInstance(effect.getEffectType(), Math.max((int) (effect.getDuration() * ratioTaken), 1), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
            //update effect in cauldron
            cauldronEffects.add(new StatusEffectInstance(effect.getEffectType(), (int) Math.max(effect.getDuration() * (1 - ratioTaken), 1), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
        });

        this.setEffects(cauldronEffects);

        return new PotionContentsComponent(this.getPotion().potion(), Optional.of(this.getColor()), outputEffects);
    }

    public NbtCompound writeNBT(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        return writeNBT(nbt, registryLookup);
    }
    public NbtCompound writeNBT(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.put("fluid", FluidVariant.CODEC.encode(this.fluid, registryLookup.getOps(NbtOps.INSTANCE), nbt).result().get());
        return nbt;
    }

    // TODO: Take another look as to how it handles which potion it actually is, like, the 'potion' of PotionContentsComponent
    public static FluidInstance fromNBT(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        FluidVariant fluid = buildFluidVariant(Fluids.BREWING_FLUID, new PotionContentsComponent(net.minecraft.potion.Potions.WATER));
        if (nbt.contains("fluid")) fluid = FluidVariant.CODEC.decode(registryLookup.getOps(NbtOps.INSTANCE), nbt.get("fluid")).result().get().getFirst();
        return new FluidInstance(fluid);
    }


}
