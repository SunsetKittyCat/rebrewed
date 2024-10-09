package net.afternooncats.rebrewed.block;

import net.afternooncats.rebrewed.Rebrewed;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockEntityTypes {
    public static final BlockEntityType<ConcoctionCauldronBlockEntity> CONCOCTION_CAULDRON = register(
            "concoction_cauldron",
            BlockEntityType.Builder.create(
                    ConcoctionCauldronBlockEntity::new,
                    Blocks.CONCOCTION_CAULDRON
            ).build()
    );

    public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Rebrewed.MOD_ID, name),
                blockEntityType
        );
    }

    public static void initialize() {}
}
