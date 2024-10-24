package net.afternooncats.rebrewed.fluid;

import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidInstance {
    private final AbstractCauldronFluid type;
    private int color;
    private int temp;
    private boolean isWet;
    private static final int maxQuantity = 3;
    private int quantity;
    private PotionContentsComponent effects;
    private NbtCompound nbt;

    public FluidInstance(AbstractCauldronFluid type, int amount) {
        this.type = type;
        this.color = this.type.getDefColor();
        this.temp = this.type.getDefTempType();
        this.isWet = this.type.isWet();
        this.quantity = amount;
        this.effects = new PotionContentsComponent(Optional.empty(), Optional.empty(), List.of());
    }

    public FluidInstance(AbstractCauldronFluid type, int amount, PotionContentsComponent effectInstances) {
        this.type = type;
        this.color = this.type.getDefColor();
        this.temp = this.type.getDefTempType();
        this.isWet = this.type.isWet();
        this.quantity = amount;
        this.effects = effectInstances;
    }

    public FluidInstance(AbstractCauldronFluid type, int color, int temp, boolean isWet, int amount, PotionContentsComponent effects) {
        this.type = type;
        this.color = color;
        this.temp = temp;
        this.isWet = isWet;
        this.quantity = amount;
        this.effects = effects;
    }

    public int getLevel() {
        return quantity;
    }
    public boolean setLevel(int newLevel) {
        if((newLevel < 0) || (maxQuantity < newLevel)) {
            return false;
        }
        this.quantity = newLevel;
        return true;
    }
    public boolean modifyLevel(int amount) {
        int newLevel = this.quantity + amount;
        if((newLevel < 0) || (maxQuantity < newLevel)) {
            return false;
        }
        this.quantity = newLevel;
        return true;
    }

    public AbstractCauldronFluid getType() {
        return type;
    }
    //maybe potentially add a setType but not right now.


    public int getColor() {
        return color;
    }
    private void addColor(int newColor) {
        int level = this.getLevel();
        int a = (ColorHelper.Argb.getAlpha(this.getColor()) * (level-1)) + ColorHelper.Argb.getAlpha(newColor);
        int r = (ColorHelper.Argb.getRed(this.getColor()) * (level-1)) + ColorHelper.Argb.getRed(newColor);
        int g = (ColorHelper.Argb.getGreen(this.getColor()) * (level-1)) + ColorHelper.Argb.getGreen(newColor);
        int b = (ColorHelper.Argb.getBlue(this.getColor()) * (level-1)) + ColorHelper.Argb.getBlue(newColor);

        this.color = ColorHelper.Argb.getArgb(a/level, r/level, g/level, b/level);
    }

    //Temperature control
    public int getTemp() {
        return temp;
    }
    public void setTemp(int newTemp) {
        this.temp = newTemp;
    }
    public boolean cool() {
        if (this.temp <= -1) return false;
        this.temp--;
        return true;
    }
    public boolean heat() {
        if (this.temp >= 1) return false;
        this.temp++;
        return true;
    }

    public boolean isWet() {
        return isWet;
    }
    public void setWet(boolean newWetness) {
        this.isWet = newWetness;
    }

    public void addPotion(PotionContentsComponent newPotion) {
        this.addEffect(newPotion);
        this.addColor(newPotion.getColor());
    }
    private void addEffect(PotionContentsComponent newEffect) {
        boolean alreadyInCauldron = false;

        ArrayList<StatusEffectInstance> fluidEffects = new ArrayList<>();

        for (StatusEffectInstance fluidEffect : this.effects.getEffects()) {
            fluidEffects.add(fluidEffect);

            if (alreadyInCauldron)
                continue;

            for (StatusEffectInstance effect : newEffect.getEffects())
            {
                if (effect == fluidEffect)
                {
                    int amp = Math.max(fluidEffect.getAmplifier(), effect.getAmplifier());
                    int duration = (int) ((fluidEffect.getDuration() * Math.pow(1d/4, (amp - fluidEffect.getAmplifier()))) + (effect.getDuration() * Math.pow(1d/4, (amp - effect.getAmplifier()))));
                    fluidEffects.removeLast();
                    fluidEffects.add(new StatusEffectInstance(fluidEffect.getEffectType(), duration, amp, fluidEffect.isAmbient(), fluidEffect.shouldShowParticles(), fluidEffect.shouldShowIcon()));
                    alreadyInCauldron = true;
                    break;
                }
            }
        }

        if (!alreadyInCauldron)
            newEffect.forEachEffect(fluidEffects::add);

        this.effects = new PotionContentsComponent(Optional.empty(), Optional.empty(), fluidEffects);
    }

    public PotionContentsComponent removePotion() {
        ArrayList<StatusEffectInstance> outputEffects = new ArrayList<>();
        ArrayList<StatusEffectInstance> cauldronEffects = new ArrayList<>();

        for (StatusEffectInstance effect : this.effects.getEffects()) {
            //add effect to potion list
            outputEffects.add(new StatusEffectInstance(effect.getEffectType(), effect.getDuration()/this.getLevel(), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
            //update effect in cauldron
            cauldronEffects.add(new StatusEffectInstance(effect.getEffectType(), (effect.getDuration()/this.getLevel())*(this.getLevel()+1), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
        }

        this.effects = new PotionContentsComponent(Optional.empty(), Optional.empty(), cauldronEffects);

        return new PotionContentsComponent(Optional.of(Potions.WATER), Optional.of(this.color), outputEffects);
    }

    public NbtCompound writeNBT() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("type", CauldronFluids.CAULDRON_FLUID.getId(this.type).toString());
        nbt.putInt("color", this.color);
        nbt.putInt("temperature", this.temp);
        nbt.putBoolean("isWet", this.isWet);
        nbt.putInt("quantity", this.quantity);
        NbtList effects = new NbtList();
        this.effects.forEachEffect(effect -> effects.add(effect.writeNbt()));
        nbt.put("effects", effects);
        return nbt;
    }

    public static FluidInstance fromNBT(NbtCompound nbt) {
        AbstractCauldronFluid type = CauldronFluids.BREWING_FLUID;
        if (nbt.contains("type")) type = CauldronFluids.CAULDRON_FLUID.get(Identifier.of(nbt.getString("type")));

        int color = type.getDefColor();
        if (nbt.contains("color")) color = nbt.getInt("color");

        int temp = type.getDefTempType();
        if (nbt.contains("temperature")) temp = nbt.getInt("temperature");

        boolean isWet = type.isWet();
        if (nbt.contains("isWet")) isWet = nbt.getBoolean("isWet");

        int quantity = 1;
        if (nbt.contains("quantity")) quantity = nbt.getInt("quantity");

        ArrayList<StatusEffectInstance> effects = new ArrayList<>();
        if (nbt.contains("effects", NbtElement.LIST_TYPE))
            ((NbtList) nbt.get("effects")).forEach(nbtElement -> effects.add(StatusEffectInstance.fromNbt((NbtCompound) nbtElement)));

        return new FluidInstance(type, color, temp, isWet, quantity, new PotionContentsComponent(Optional.empty(), Optional.empty(), effects));
    }


}
