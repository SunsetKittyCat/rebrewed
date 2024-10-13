package net.afternooncats.rebrewed.fluid;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;

public class FluidInstance {
    private final AbstractCauldronFluid type;
    private int color;
    private AbstractCauldronFluid.FluidTemp temp;
    private boolean isWet;
    private static final int maxQuantity = 3;
    private int quantity;
    private ArrayList<StatusEffectInstance> effects;

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
        this.quantity = amount;
        this.effects = effectInstances;
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
    public void updateColor() {
        int r = 0;
        int g = 0;
        int b = 0;
        for (StatusEffectInstance instance : effects) {
            r += instance.getDuration() * ColorHelper.Argb.getRed(instance.getEffectType().value().getColor());
            g += instance.getDuration() * ColorHelper.Argb.getGreen(instance.getEffectType().value().getColor());
            b += instance.getDuration() * ColorHelper.Argb.getBlue(instance.getEffectType().value().getColor());
        }
        this.color = ColorHelper.Argb.getArgb(r/ effects.size(), g/ effects.size(), b/ effects.size());
    }

    //Temperature control
    public AbstractCauldronFluid.FluidTemp getTemp() {
        return temp;
    }
    public void setTemp(AbstractCauldronFluid.FluidTemp newTemp) {
        this.temp = newTemp;
    }
    public boolean cool() {
        if (this.temp == AbstractCauldronFluid.FluidTemp.COLD) return false;
        else if (this.temp == AbstractCauldronFluid.FluidTemp.NORMAL) this.temp = AbstractCauldronFluid.FluidTemp.COLD;
        else this.temp = AbstractCauldronFluid.FluidTemp.NORMAL;
        return true;
    }
    public boolean heat() {
        if (this.temp == AbstractCauldronFluid.FluidTemp.HOT) return false;
        else if (this.temp == AbstractCauldronFluid.FluidTemp.NORMAL) this.temp = AbstractCauldronFluid.FluidTemp.HOT;
        else this.temp = AbstractCauldronFluid.FluidTemp.NORMAL;
        return true;
    }

    public boolean isWet() {
        return isWet;
    }
    public void setWet(boolean newWetness) {
        this.isWet = newWetness;
    }

    public void addEffect(StatusEffectInstance newEffect) {
        this.effects.add(newEffect);
        this.updateColor();
    }
}
