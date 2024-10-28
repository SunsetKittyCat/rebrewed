package net.afternooncats.rebrewed.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

import java.util.Objects;

public class CauldronRecipeSerializer<T extends CauldronRecipe> implements RecipeSerializer<T> {
    private final CauldronRecipe.RecipeFactory<T> recipeFactory;
    private final MapCodec<T> codec;
    private final PacketCodec<RegistryByteBuf, T> packetCodec;

    public CauldronRecipeSerializer(CauldronRecipe.RecipeFactory<T> recipeFactory) {
        this.recipeFactory = recipeFactory;
        this.codec = RecordCodecBuilder.mapCodec((instance) -> {
            Objects.requireNonNull(recipeFactory);
            return instance.group(
                    Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(CauldronRecipe::getIngredient),
                    Codec.BOOL.fieldOf("requiresHeating").forGetter(CauldronRecipe::getHeatReqs),
                    ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("result").forGetter(CauldronRecipe::getResultI)
            ).apply(instance, recipeFactory::create);
        });
        this.packetCodec = PacketCodec.ofStatic(this::write, this::read);
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public PacketCodec<RegistryByteBuf, T> packetCodec() {
        return this.packetCodec;
    }

    private T read(RegistryByteBuf buf) {
        Ingredient ingredient = (Ingredient)Ingredient.PACKET_CODEC.decode(buf);
        boolean requiresHeating = (boolean) PacketCodecs.BOOL.decode(buf);
        ItemStack itemStack = (ItemStack)ItemStack.PACKET_CODEC.decode(buf);
        return this.recipeFactory.create(ingredient, requiresHeating, itemStack);
    }

    private void write(RegistryByteBuf buf, T recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
        PacketCodecs.BOOL.encode(buf, recipe.requiresHeating);
        ItemStack.PACKET_CODEC.encode(buf, recipe.result);
    }
}
