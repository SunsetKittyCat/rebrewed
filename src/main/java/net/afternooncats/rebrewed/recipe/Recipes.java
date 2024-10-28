package net.afternooncats.rebrewed.recipe;

import net.afternooncats.rebrewed.Rebrewed;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Recipes {

    public static RecipeSerializer<BrewingRecipe> BREWING_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(Rebrewed.MOD_ID, BrewingRecipe.Type.ID), new CauldronRecipeSerializer<BrewingRecipe>(BrewingRecipe::new));
    public static RecipeType<BrewingRecipe> BREWING_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(Rebrewed.MOD_ID, BrewingRecipe.Type.ID), BrewingRecipe.Type.INSTANCE);


    public static void initialize() {
    }
}
