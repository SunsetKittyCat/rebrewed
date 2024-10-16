package net.afternooncats.rebrewed.fluid;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;

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

    public void addEffect(StatusEffectInstance newEffect) {
        this.effects.add(newEffect);
        this.updateColor();
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
            for (net.minecraft.nbt.NbtElement nbtElement : list) {
                effects.add(StatusEffectInstance.fromNbt((NbtCompound) nbtElement));
            }
        }
        return new FluidInstance(type, color, temp, isWet, quantity, effects);
    }


}
