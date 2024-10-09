package net.afternooncats.rebrewed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.afternooncats.rebrewed.block.Blocks;
import net.afternooncats.rebrewed.block.ConcoctionCauldronBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @Inject(method = "create", at = @At("TAIL"))
    private static void rebrewed$onCreate(CallbackInfoReturnable<BlockColors> cir, @Local BlockColors blockColors) {
        blockColors.registerColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? ConcoctionCauldronBlockEntity.getColor(world, pos) : -1,
                Blocks.CONCOCTION_CAULDRON
        );
    }
}
