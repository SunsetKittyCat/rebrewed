package net.afternooncats.rebrewed.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.BlockRenderView;

public class ConcoctionCauldronBlockEntity extends BlockEntity {
    public int color = ColorHelper.Argb.getArgb(255, 0, 255);

    public ConcoctionCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.CONCOCTION_CAULDRON, pos, state);
    }

    @Environment(EnvType.CLIENT)
    public static int getColor(BlockRenderView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ConcoctionCauldronBlockEntity)) return -1;

        return ((ConcoctionCauldronBlockEntity) blockEntity).color;
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