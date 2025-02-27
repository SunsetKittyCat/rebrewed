package net.afternooncats.rebrewed.block;

import net.afternooncats.rebrewed.Rebrewed;
import net.afternooncats.rebrewed.fluid.Fluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class Blocks {
    public static final Block CONCOCTION_CAULDRON = register(
            "concoction_cauldron",
            new ConcoctionCauldronBlock(AbstractBlock.Settings.create()),
            false
    );
    public static final Block BREWING_FLUID = register("brewing_fluid", new FluidBlock(Fluids.BREWING_FLUID, AbstractBlock.Settings.create().mapColor(MapColor.WATER_BLUE).replaceable().noCollision().strength(0.0F).pistonBehavior(PistonBehavior.DESTROY).dropsNothing().liquid().sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)), false);

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