package net.afternooncats.rebrewed.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.world.World;

import java.util.Optional;

public class RecipeHelper {
    public static boolean canBrewStack(World world, ItemStack stack) {
        Optional<RecipeEntry<BrewingRecipe>> recipe = world.getRecipeManager().getFirstMatch(Recipes.BREWING_TYPE, new SingleStackRecipeInput(stack), world);
        return recipe.isPresent();
    }

    public static ItemStack brewItemStack(World world, ItemStack input) {
        Optional<RecipeEntry<BrewingRecipe>> recipe = world.getRecipeManager().getFirstMatch(Recipes.BREWING_TYPE, new SingleStackRecipeInput(input), world);
        return recipe.map(brewingRecipeRecipeEntry -> new ItemStack(brewingRecipeRecipeEntry.value().getResult(null).getItem(), input.getCount())).orElse(null);
    }
}
