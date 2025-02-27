package net.afternooncats.rebrewed.fluid;

import net.afternooncats.rebrewed.potion.Potions;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.Optional;

public class FluidInstance {
    private static final int maxQuantity = 3;
    private FluidVariant fluid;

    public FluidInstance(Fluid fluid, int amount) {
        this(fluid, amount, new ArrayList<>());
    }

    public FluidInstance(Fluid fluid, int amount, ArrayList<StatusEffectInstance> effects) {
        this.fluid = buildFluidVariant(fluid,
                fluid.getDefaultState().getBlockState().getBlock().getDefaultMapColor().color,
                amount,
                effects);
    }

    public static FluidVariant buildFluidVariant(Fluid fluid, int quantity, int color, ArrayList<StatusEffectInstance> effects) {
        return buildFluidVariant(fluid, quantity, Potions.COMPOSITE, color, effects);
    }
    public static FluidVariant buildFluidVariant(Fluid fluid, int quantity, RegistryEntry<Potion> potion, int color, ArrayList<StatusEffectInstance> effects) {
        ComponentChanges.Builder comp = ComponentChanges.builder();
        //i just need a place to store the int for the quantity, "max stack size" is close enough
        comp.add(DataComponentTypes.MAX_STACK_SIZE, quantity);
        //stg these components are probably great... but...
        comp.add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(
                Optional.of(potion),
                Optional.of(color),
                effects));
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

    public int getQuantity() {
        return !this.getComponents().isEmpty() && this.getComponents().get(DataComponentTypes.MAX_STACK_SIZE).isPresent() ? this.getComponents().get(DataComponentTypes.MAX_STACK_SIZE).get() : 1;
    }
    public int getColor() {
        return !this.getComponents().isEmpty() && this.getComponents().get(DataComponentTypes.POTION_CONTENTS).isPresent() ? this.getComponents().get(DataComponentTypes.POTION_CONTENTS).get().getColor() : this.getFluid().getDefaultState().getBlockState().getBlock().getDefaultMapColor().color;
    }
    public ArrayList<StatusEffectInstance> getEffects() {
        if (this.getComponents().isEmpty() || this.getComponents().get(DataComponentTypes.POTION_CONTENTS).isEmpty()) return new ArrayList<>();
        Iterable<StatusEffectInstance> statuses = this.getComponents().get(DataComponentTypes.POTION_CONTENTS).get().getEffects();
        ArrayList<StatusEffectInstance> effects = new ArrayList<>();
        statuses.forEach(effects::add);
        return effects;
    }

    public void setColor(int newColor) {
        this.fluid = buildFluidVariant(this.getFluid(), this.getQuantity(), newColor, this.getEffects());
    }
    public boolean setQuantity(int newLevel) {
        if((newLevel < 0) || (maxQuantity < newLevel)) {
            return false;
        }
        this.fluid = buildFluidVariant(this.getFluid(), newLevel, this.getColor(), this.getEffects());
        return true;
    }
    public void setEffects(ArrayList<StatusEffectInstance> effects) {
        this.fluid = buildFluidVariant(this.getFluid(), this.getQuantity(), this.getColor(), effects);
    }

    private void addColor(int newColor) {
        int level = this.getQuantity();
        int a = (ColorHelper.Argb.getAlpha(this.getColor()) * (level-1)) + ColorHelper.Argb.getAlpha(newColor);
        int r = (ColorHelper.Argb.getRed(this.getColor()) * (level-1)) + ColorHelper.Argb.getRed(newColor);
        int g = (ColorHelper.Argb.getGreen(this.getColor()) * (level-1)) + ColorHelper.Argb.getGreen(newColor);
        int b = (ColorHelper.Argb.getBlue(this.getColor()) * (level-1)) + ColorHelper.Argb.getBlue(newColor);

        this.setColor(ColorHelper.Argb.getArgb(a/level, r/level, g/level, b/level));
    }
    public boolean modifyLevel(int amount) {
        int newLevel = this.getQuantity() + amount;
        if((newLevel < 0) || (maxQuantity < newLevel)) {
            return false;
        }
        this.fluid = buildFluidVariant(this.getFluid(), newLevel, this.getColor(), this.getEffects());
        return true;
    }

    public void addPotion(PotionContentsComponent newPotion) {
//        for (StatusEffectInstance effect : newPotion.getEffects()) {
//            this.addEffect(effect);
//        }
        this.addEffects((StatusEffectInstance) newPotion.getEffects());
        this.addColor(newPotion.getColor());
    }
    private void addEffect(StatusEffectInstance newEffect) {
        ArrayList<StatusEffectInstance> currentEffects = this.getEffects();
        boolean alreadyInCauldron = false;
        for (StatusEffectInstance fluidEffect : currentEffects) {
            if (fluidEffect.getEffectType() == newEffect.getEffectType()) {
                int amp = Math.max(fluidEffect.getAmplifier(), newEffect.getAmplifier());
                int duration = (int) ((fluidEffect.getDuration() * Math.pow(1d / 4, (amp - fluidEffect.getAmplifier()))) + (newEffect.getDuration() * Math.pow(1d / 4, (amp - newEffect.getAmplifier()))));
                fluidEffect = new StatusEffectInstance(fluidEffect.getEffectType(), duration, amp, fluidEffect.isAmbient(), fluidEffect.shouldShowParticles(), fluidEffect.shouldShowIcon());
                alreadyInCauldron = true;
                break;
            }
        }
        if (!alreadyInCauldron) {
            currentEffects.add(newEffect);
        }
        this.setEffects(currentEffects);
    }
    private void addEffects(StatusEffectInstance... newEffects) {
        ArrayList<StatusEffectInstance> currentEffects = this.getEffects();
        for (StatusEffectInstance newEffect : newEffects) {
            boolean alreadyInCauldron = false;
            for (StatusEffectInstance fluidEffect : currentEffects) {
                if (fluidEffect.getEffectType() == newEffect.getEffectType()) {
                    int amp = Math.max(fluidEffect.getAmplifier(), newEffect.getAmplifier());
                    int duration = (int) ((fluidEffect.getDuration() * Math.pow(1d / 4, (amp - fluidEffect.getAmplifier()))) + (newEffect.getDuration() * Math.pow(1d / 4, (amp - newEffect.getAmplifier()))));
                    fluidEffect = new StatusEffectInstance(fluidEffect.getEffectType(), duration, amp, fluidEffect.isAmbient(), fluidEffect.shouldShowParticles(), fluidEffect.shouldShowIcon());
                    alreadyInCauldron = true;
                    break;
                }
            }
            if (!alreadyInCauldron) {
                currentEffects.add(newEffect);
            }
        }
        this.setEffects(currentEffects);
    }

    public PotionContentsComponent removePotion() {
        ArrayList<StatusEffectInstance> effects = new ArrayList<>();
        ArrayList<StatusEffectInstance> currentEffects = this.getEffects();
        for (StatusEffectInstance effect : currentEffects) {
            //add effect to potion list
            effects.add(new StatusEffectInstance(effect.getEffectType(), effect.getDuration()/this.getQuantity(), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
            //update effect in cauldron
            effect = new StatusEffectInstance(effect.getEffectType(), (effect.getDuration()/this.getQuantity())*(this.getQuantity()+1), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon());
        }
        this.setEffects(currentEffects);
        return new PotionContentsComponent(Optional.of(Potions.COMPOSITE), Optional.of(this.getColor()), effects);
    }

    public NbtCompound writeNBT(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        return writeNBT(nbt, registryLookup);
    }
    public NbtCompound writeNBT(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putString("fluid", this.fluid.getFluid().toString());
        ComponentChanges.CODEC.encode(this.getComponents(), registryLookup.getOps(NbtOps.INSTANCE), nbt);
        nbt.putInt("quantity", this.getQuantity());
        NbtList effects = new NbtList();
        for (StatusEffectInstance effect : this.getEffects()) {
            effects.add(effect.writeNbt());
        }
        nbt.put("effects", effects);
        return nbt;
    }

    public static FluidInstance fromNBT(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Fluid fluid = Fluids.BREWING_FLUID;
        if (nbt.contains("fluid")) fluid = Registries.FLUID.get(Identifier.of(nbt.getString("fluid")));

        int quantity = 1;
        if (nbt.contains("quantity")) quantity = nbt.getInt("quantity");

        ArrayList<StatusEffectInstance> effects = new ArrayList<>();
        if (nbt.contains("effects")) {
            NbtList list = (NbtList) nbt.get("effects");
            for (NbtElement nbtElement : list) {
                effects.add(StatusEffectInstance.fromNbt((NbtCompound) nbtElement));
            }
        }
        return new FluidInstance(fluid, quantity, effects);
    }


}
