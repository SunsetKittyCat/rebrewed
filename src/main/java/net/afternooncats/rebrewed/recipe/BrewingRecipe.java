package net.afternooncats.rebrewed.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;

public class BrewingRecipe extends CauldronRecipe {

    public BrewingRecipe(RecipeType<? extends BrewingRecipe> type, Ingredient ingredient, boolean requiresHeating, ItemStack result) {
        super(type, ingredient, requiresHeating, result);
    }

    public BrewingRecipe(Ingredient ingredient, boolean requiresHeating, ItemStack result) {
        super(Type.INSTANCE, ingredient, requiresHeating, result);
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
