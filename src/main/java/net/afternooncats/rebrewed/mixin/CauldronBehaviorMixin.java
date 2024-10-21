package net.afternooncats.rebrewed.mixin;

import net.afternooncats.rebrewed.block.ConcoctionCauldronBlock;
import net.afternooncats.rebrewed.block.ConcoctionCauldronBlockEntity;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemActionResult;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {

    @Inject(method = "registerBehavior", at = @At(value = "TAIL"))
    private static void addConcoctionBehavior(CallbackInfo ci) {
        Map<Item, CauldronBehavior> cMap = ConcoctionCauldronBlock.behaviorMap.map();
        cMap.put(Items.POTION, (CauldronBehavior)(state, world, pos, player, hand, stack) -> {
            PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (potionContentsComponent != null && blockEntity instanceof ConcoctionCauldronBlockEntity) {
                if (!world.isClient) {
                    //Add the fluid and update the level blockstate
                    boolean didAdd = ConcoctionCauldronBlockEntity.addFluid(world, pos, potionContentsComponent);
                    world.setBlockState(pos, state.with(ConcoctionCauldronBlock.LEVEL, ConcoctionCauldronBlockEntity.getFluidLevel(world, pos)));

                    //if it didnt add, dont continue
                    if (!didAdd) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

                    //Item stuff
                    Item item = stack.getItem();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));

                    //Stat stuff
                    player.incrementStat(Stats.USE_CAULDRON);
                    player.incrementStat(Stats.USED.getOrCreateStat(item));

                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);

                }

                return ItemActionResult.success(world.isClient);
            } else {
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        });
        cMap.put(Items.GLASS_BOTTLE, (CauldronBehavior)(state, world, pos, player, hand, stack) -> {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ConcoctionCauldronBlockEntity) {
                if (!world.isClient) {
                    //Get the potion and update the level blockstate
                    PotionContentsComponent contents = ConcoctionCauldronBlockEntity.removeFluid(world, pos);
                    world.setBlockState(pos, state.with(ConcoctionCauldronBlock.LEVEL, ConcoctionCauldronBlockEntity.getFluidLevel(world, pos)));

                    //Item stuff
                    ItemStack potion = new ItemStack(Items.POTION);
                    potion.set(DataComponentTypes.POTION_CONTENTS, contents);
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, potion));

                    //Stat stuff
                    player.incrementStat(Stats.USE_CAULDRON);
                    player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));

                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);

                }

                return ItemActionResult.success(world.isClient);
            } else {
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        });

    }
}
