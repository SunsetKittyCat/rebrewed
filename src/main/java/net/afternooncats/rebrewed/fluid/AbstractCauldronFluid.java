package net.afternooncats.rebrewed.fluid;

public class AbstractCauldronFluid {
    private final int defColor;
    private final FluidTemp defTempType;
    private final boolean defIsWet;

    public AbstractCauldronFluid(int defColor, FluidTemp defTempType, boolean defWet) {
        this.defColor = defColor;
        this.defTempType = defTempType;
        this.defIsWet = defWet;
    }

    //The getters, theoretically dont need setters (for this anyway)
    public int getDefColor() {
        return defColor;
    }
    public FluidTemp getDefTempType() {
        return defTempType;
    }
    public boolean isWet() {
        return defIsWet;
    }

    public enum FluidTemp {
        HOT,
        NORMAL,
        COLD
    }
}
