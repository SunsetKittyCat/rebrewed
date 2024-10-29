package net.afternooncats.rebrewed.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public abstract class CauldronRecipe implements Recipe<SingleStackRecipeInput> {
    protected final RecipeType<?> type;
    protected final Ingredient ingredient;
    protected final String fluid;
    protected final Boolean requiresHeating;
    protected final String potionResult;
    protected final ItemStack result;

    public CauldronRecipe(RecipeType<? extends CauldronRecipe> type, Ingredient ingredient, String fluid, Boolean requiresHeating, String potionResult, ItemStack result) {
        this.type = type;
        this.fluid = fluid;
        this.requiresHeating = requiresHeating;
        this.potionResult = potionResult;
        this.result = result;
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        if (world.isClient()) {
            return false;
        }
        return this.ingredient.test(input.item());
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.ingredient);
        return defaultedList;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }
    public String getFluid() { return this.fluid; }
    public boolean getHeatReqs() { return this.requiresHeating; }
    public String getPotion() { return this.potionResult; }
    public ItemStack getResultI() {
        return this.result;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    public interface RecipeFactory<T extends CauldronRecipe> {
        T create(Ingredient ingredient, String fluidReqs, boolean requiresHeating, String potion, ItemStack result);

    }
}
