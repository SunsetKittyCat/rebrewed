package net.afternooncats.rebrewed.block;

import net.afternooncats.rebrewed.fluid.CauldronFluids;
import net.afternooncats.rebrewed.fluid.FluidInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

public class ConcoctionCauldronBlockEntity extends BlockEntity {
    public int color = ColorHelper.Argb.getArgb(255, 0, 255);
    public FluidInstance fluid = new FluidInstance(CauldronFluids.BREWING_FLUID, 0);

    public ConcoctionCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.CONCOCTION_CAULDRON, pos, state);
    }

    @Environment(EnvType.CLIENT)
    public static int getColor(BlockRenderView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ConcoctionCauldronBlockEntity)) return -1;

        return ((ConcoctionCauldronBlockEntity) blockEntity).color;
    }

    public static boolean addFluid(World world, BlockPos pos, PotionContentsComponent potionData) {
        if(world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).isEmpty()) return false;
        ConcoctionCauldronBlockEntity bE = world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).get();
        if(!bE.fluid.modifyLevel(1)) return false;
        bE.fluid.addPotion(potionData);
        bE.color = bE.fluid.getColor();
        bE.markDirty();
        return true;
    }

    //returns just water if theres a problem
    public static PotionContentsComponent removeFluid(World world, BlockPos pos) {
        if(world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).isEmpty()) return new PotionContentsComponent(Potions.WATER);
        ConcoctionCauldronBlockEntity bE = world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).get();
        PotionContentsComponent potionContentsComponent = bE.fluid.removePotion();
        bE.fluid.modifyLevel(-1);
        bE.markDirty();
        return potionContentsComponent;
    }

    public static int getFluidLevel(World world, BlockPos pos) {
        if(world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).isEmpty()) return 0;
        return world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).get().fluid.getLevel();
    }

    @Override
    public void markDirty() {
        if (world == null) return;

        if (world.isClient())
            MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        else if (world instanceof ServerWorld) ((ServerWorld) world).getChunkManager().markForUpdate(pos);

        super.markDirty();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("color", color);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        color = nbt.getInt("color");

        markDirty();
    }
}