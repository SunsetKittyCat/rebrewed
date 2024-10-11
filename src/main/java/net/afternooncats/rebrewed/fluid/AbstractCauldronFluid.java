package net.afternooncats.rebrewed.fluid;

public class AbstractCauldronFluid {
    private int color;
    private FluidTemp tempType;
    private boolean isWet;
    //also vanilla fluids
    //fluid properties:
        //potion effects

    public AbstractCauldronFluid(int color, FluidTemp tempType, boolean wet) {
        this.color = color;
        this.tempType = tempType;
        this.isWet = wet;
    }

    //The getters, theoretically dont need setters (for this anyway)
    public int getColor() {
        return color;
    }
    public FluidTemp getTempType() {
        return tempType;
    }
    public boolean isWet() {
        return isWet;
    }

    public enum FluidTemp {
        HOT,
        NORMAL,
        COLD
    }
}
