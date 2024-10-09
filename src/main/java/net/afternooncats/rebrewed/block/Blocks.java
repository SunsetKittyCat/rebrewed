package net.afternooncats.rebrewed.block;

import net.afternooncats.rebrewed.Rebrewed;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class Blocks {
    public static final Block CONCOCTION_CAULDRON = register(
            "concoction_cauldron",
            new ConcoctionCauldronBlock(AbstractBlock.Settings.create()),
            false
    );

    public static Block register(String name, Block block, boolean shouldRegisterItem) {
        Identifier id = Identifier.of(Rebrewed.MOD_ID, name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize() {}
}