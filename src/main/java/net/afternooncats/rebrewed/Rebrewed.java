package net.afternooncats.rebrewed;

import net.afternooncats.rebrewed.block.BlockEntityTypes;
import net.afternooncats.rebrewed.block.Blocks;
import net.afternooncats.rebrewed.fluid.CauldronFluids;
import net.afternooncats.rebrewed.potion.Potions;
import net.afternooncats.rebrewed.recipe.Recipes;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rebrewed implements ModInitializer {
	public static final String MOD_ID = "rebrewed";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Blocks.initialize();
		BlockEntityTypes.initialize();
		CauldronFluids.registerCauldronFluids();
		Potions.initialize();
		Recipes.initialize();
	}
}