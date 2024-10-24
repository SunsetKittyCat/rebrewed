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
import java.util.Optional;

public class FluidInstance {
    private final AbstractCauldronFluid type;
    private int color;
    private int temp;
    private boolean isWet;
    private static final int maxQuantity = 3;
    private int quantity;
    private ArrayList<StatusEffectInstance> effects;
    private NbtCompound nbt;

    public FluidInstance(AbstractCauldronFluid type, int amount) {
        this.type = type;
        this.color = this.type.getDefColor();
        this.temp = this.type.getDefTempType();
        this.isWet = this.type.isWet();
        this.quantity = amount;
        this.effects = new ArrayList<>();
    }

    public FluidInstance(AbstractCauldronFluid type, int amount, ArrayList<StatusEffectInstance> effectInstances) {
        this.type = type;
        this.color = this.type.getDefColor();
        this.temp = this.type.getDefTempType();
        this.isWet = this.type.isWet();
        this.quantity = amount;
        this.effects = effectInstances;
    }

    public FluidInstance(AbstractCauldronFluid type, int color, int temp, boolean isWet, int amount, ArrayList<StatusEffectInstance> effects) {
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
        for (StatusEffectInstance effect : newPotion.getEffects()) {
            this.addEffect(effect);
        }
        this.addColor(newPotion.getColor());
    }
    private void addEffect(StatusEffectInstance newEffect) {
        boolean alreadyInCauldron = false;
        for (StatusEffectInstance fluidEffect : this.effects) {
            if (fluidEffect.getEffectType() == newEffect.getEffectType()) {
                int amp = Math.max(fluidEffect.getAmplifier(), newEffect.getAmplifier());
                int duration = (int) ((fluidEffect.getDuration() * Math.pow(1d/4, (amp - fluidEffect.getAmplifier()))) + (newEffect.getDuration() * Math.pow(1d/4, (amp - newEffect.getAmplifier()))));
                fluidEffect = new StatusEffectInstance(fluidEffect.getEffectType(), duration, amp, fluidEffect.isAmbient(), fluidEffect.shouldShowParticles(), fluidEffect.shouldShowIcon());
                alreadyInCauldron = true;
                break;
            }
        }
        if (!alreadyInCauldron) {
            this.effects.add(newEffect);
        }
    }

    public PotionContentsComponent removePotion() {
        ArrayList<StatusEffectInstance> effects = new ArrayList<>();
        for (StatusEffectInstance effect : this.effects) {
            //add effect to potion list
            effects.add(new StatusEffectInstance(effect.getEffectType(), effect.getDuration()/this.getLevel(), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
            //update effect in cauldron
            effect = new StatusEffectInstance(effect.getEffectType(), (effect.getDuration()/this.getLevel())*(this.getLevel()+1), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon());
        }
        return new PotionContentsComponent(Optional.of(Potions.WATER), Optional.of(this.color), effects);
    }

    public NbtCompound writeNBT() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("type", CauldronFluids.CAULDRON_FLUID.getId(this.type).toString());
        nbt.putInt("color", this.color);
        nbt.putInt("temperature", this.temp);
        nbt.putBoolean("isWet", this.isWet);
        nbt.putInt("quantity", this.quantity);
        NbtList effects = new NbtList();
        for (StatusEffectInstance effect : this.effects) {
            effects.add(effect.writeNbt());
        }
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
        if (nbt.contains("effects")) {
            StatusEffectInstance[] tmpEffct;
            NbtList list = (NbtList) nbt.get("effects");
            for (NbtElement nbtElement : list) {
                effects.add(StatusEffectInstance.fromNbt((NbtCompound) nbtElement));
            }
        }
        return new FluidInstance(type, color, temp, isWet, quantity, effects);
    }


}
