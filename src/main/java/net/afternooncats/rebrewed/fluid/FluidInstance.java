package net.afternooncats.rebrewed.fluid;

import net.afternooncats.rebrewed.Rebrewed;
import net.afternooncats.rebrewed.block.ConcoctionCauldronBlock;
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
    private static final int maxQuantity = ConcoctionCauldronBlock.MAX_LEVEL;
    private FluidVariant fluid;

    public FluidInstance(Fluid fluid, int amount) {
        this(fluid, amount, new ArrayList<>());
    }
    public FluidInstance(Fluid fluid, int amount, ArrayList<StatusEffectInstance> effects) {
        this.fluid = buildFluidVariant(fluid,
                amount,
                fluid.getDefaultState().getBlockState().getBlock().getDefaultMapColor().color,
                effects);
    }
    public FluidInstance(FluidVariant fluid) {
        this.fluid = fluid;
    }

    public static FluidVariant buildFluidVariant(Fluid fluid, int quantity, int color, ArrayList<StatusEffectInstance> effects) {
        return buildFluidVariant(fluid, quantity, Potions.COMPOSITE, color, effects);
    }
    public static FluidVariant buildFluidVariant(Fluid fluid, int quantity, RegistryEntry<Potion> potion, int color, ArrayList<StatusEffectInstance> effects) {
        return buildFluidVariant(fluid, quantity, new PotionContentsComponent(
                Optional.of(potion),
                Optional.of(color),
                effects));
    }
    public static FluidVariant buildFluidVariant(Fluid fluid, int quantity, PotionContentsComponent potion) {
        ComponentChanges.Builder comp = ComponentChanges.builder();
        comp.add(DataComponentTypes.MAX_STACK_SIZE, quantity);
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

    //Defaults to 1
    public int getQuantity() {
        return !this.getComponents().isEmpty() && this.getComponents().get(DataComponentTypes.MAX_STACK_SIZE).isPresent() ? this.getComponents().get(DataComponentTypes.MAX_STACK_SIZE).get() : 1;
    }
    public int getColor() {
        return !this.getComponents().isEmpty() && this.getComponents().get(DataComponentTypes.POTION_CONTENTS).isPresent() ? this.getComponents().get(DataComponentTypes.POTION_CONTENTS).get().getColor() : this.getFluid().getDefaultState().getBlockState().getBlock().getDefaultMapColor().color;
    }
    public PotionContentsComponent getPotion() {
        return !this.getComponents().isEmpty() && this.getComponents().get(DataComponentTypes.POTION_CONTENTS).isPresent() ? this.getComponents().get(DataComponentTypes.POTION_CONTENTS).get() : new PotionContentsComponent(Potions.COMPOSITE);
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
    public void setPotion(PotionContentsComponent potion) {
        this.fluid = buildFluidVariant(this.getFluid(), this.getQuantity(), potion);
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
        ArrayList<StatusEffectInstance> fluidEffects = new ArrayList<>();

        newPotion.forEachEffect(potionEffect -> {
            boolean inCauldron = false;

            for (StatusEffectInstance cauldronEffect : this.getEffects()) {
                if (potionEffect.getEffectType() == cauldronEffect.getEffectType())
                {
                    int amp = Math.max(cauldronEffect.getAmplifier(), potionEffect.getAmplifier());
                    int duration = (int) ((cauldronEffect.getDuration() * Math.pow(1d/4, (amp - cauldronEffect.getAmplifier()))) + (potionEffect.getDuration() * Math.pow(1d/4, (amp - potionEffect.getAmplifier()))));
                    fluidEffects.add(new StatusEffectInstance(cauldronEffect.getEffectType(), duration, amp, cauldronEffect.isAmbient(), cauldronEffect.shouldShowParticles(), cauldronEffect.shouldShowIcon()));
                    inCauldron = true;
                    break;
                }
            }

            if (!inCauldron)
                fluidEffects.add(potionEffect);
        });

        this.getPotion().forEachEffect(cauldronEffect -> {
            boolean inPotion = false;

            for (StatusEffectInstance potionEffect : newPotion.getEffects()) {
                if (cauldronEffect.getEffectType() == potionEffect.getEffectType())
                {
                    inPotion = true;
                    break;
                }
            }

            if (!inPotion)
                fluidEffects.add(cauldronEffect);
        });

        this.setEffects(fluidEffects);
    }

    public PotionContentsComponent removePotion() {
        ArrayList<StatusEffectInstance> outputEffects = new ArrayList<>();
        ArrayList<StatusEffectInstance> cauldronEffects = new ArrayList<>();

        // TODO: Create solution for infinitely-diluting potions (minimum duration is 1)
        this.getPotion().forEachEffect(effect -> {
            //add effect to potion list
            outputEffects.add(new StatusEffectInstance(effect.getEffectType(), Math.max((int) (effect.getDuration() / (double) this.getQuantity()), 1), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
            //update effect in cauldron
            cauldronEffects.add(new StatusEffectInstance(effect.getEffectType(), Math.max((int) (effect.getDuration() * (this.getQuantity() - 1d) / this.getQuantity()), 1), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
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
        Rebrewed.LOGGER.info(String.valueOf(nbt));
        return nbt;
    }

    public static FluidInstance fromNBT(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        FluidVariant fluid = buildFluidVariant(Fluids.BREWING_FLUID, 1, new PotionContentsComponent(net.minecraft.potion.Potions.WATER));
        if (nbt.contains("fluid")) fluid = FluidVariant.CODEC.decode(registryLookup.getOps(NbtOps.INSTANCE), nbt.get("fluid")).result().get().getFirst();
        return new FluidInstance(fluid);
    }


}
