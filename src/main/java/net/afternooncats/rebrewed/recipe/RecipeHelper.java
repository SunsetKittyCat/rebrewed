package net.afternooncats.rebrewed.recipe;

import net.afternooncats.rebrewed.Rebrewed;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

public class RecipeHelper {
    public static boolean canBrewStack(World world, ItemStack stack) {
        Optional<RecipeEntry<BrewingRecipe>> recipe = world.getRecipeManager().getFirstMatch(Recipes.BREWING_TYPE, new SingleStackRecipeInput(stack), world);
        return recipe.isPresent();
    }

    public static PotionContentsComponent brewItemStack(World world, ItemStack input) {
        Optional<RecipeEntry<BrewingRecipe>> recipe = world.getRecipeManager().getFirstMatch(Recipes.BREWING_TYPE, new SingleStackRecipeInput(input), world);
        if (recipe.isEmpty()) {
            Rebrewed.LOGGER.info("Uh oh, no recipe!");
            return PotionContentsComponent.DEFAULT;
        }
        if (recipe.get().value().getPotion().isEmpty()) {
            Rebrewed.LOGGER.info("Uh oh, no potion!");
            return PotionContentsComponent.DEFAULT;
        }
        return new PotionContentsComponent(Registries.POTION.getEntry(Identifier.of(recipe.get().value().getPotion())).get());
    }
}
