package net.afternooncats.rebrewed.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;

public class BrewingRecipe extends CauldronRecipe {

    public BrewingRecipe(RecipeType<? extends BrewingRecipe> type, Ingredient ingredient, String fluid, boolean requiresHeating, String potionResult, ItemStack result) {
        super(type, ingredient, fluid, requiresHeating, potionResult, result);
    }

    public BrewingRecipe(Ingredient ingredient, String fluid, boolean requiresHeating, String potionResult, ItemStack result) {
        super(Type.INSTANCE, ingredient, fluid, requiresHeating, potionResult, result);
    }

    @Override
    public RecipeSerializer<BrewingRecipe> getSerializer() {
        return Recipes.BREWING_SERIALIZER;
    }

    public static class Type implements RecipeType<BrewingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "brewing";
    }
}
