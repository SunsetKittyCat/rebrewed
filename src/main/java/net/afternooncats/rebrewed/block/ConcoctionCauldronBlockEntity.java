package net.afternooncats.rebrewed.block;

import net.afternooncats.rebrewed.fluid.FluidInstance;
import net.afternooncats.rebrewed.fluid.Fluids;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

import java.util.Optional;

public class ConcoctionCauldronBlockEntity extends BlockEntity {
    public int color = ColorHelper.Argb.getArgb(0, 0, 255);
    public FluidInstance fluid = new FluidInstance(Fluids.BREWING_FLUID);

    public ConcoctionCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.CONCOCTION_CAULDRON, pos, state);
    }

    @Environment(EnvType.CLIENT)
    public static int getColor(BlockRenderView world, BlockPos pos) {
        Optional<ConcoctionCauldronBlockEntity> blockEntityOptional = world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON);
        if (blockEntityOptional.isEmpty()) return -1;

        ConcoctionCauldronBlockEntity blockEntity = blockEntityOptional.get();

        return blockEntity.fluid.getColor();
    }

    public static boolean createFluid(World world, BlockPos pos, PotionContentsComponent potionData) {
        if(world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).isEmpty()) return false;
        ConcoctionCauldronBlockEntity bE = world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).get();
        bE.fluid.setPotion(potionData);
        bE.color = bE.fluid.getColor();
        bE.markDirty();
        return true;
    }

    public static boolean addFluid(World world, BlockPos pos, PotionContentsComponent potionData) {
        if(world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).isEmpty()) return false;
        ConcoctionCauldronBlockEntity bE = world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).get();
        if(!modifyFluidLevel(world, pos, 1)) return false;
        bE.fluid.addPotion(potionData, 1.0/getFluidLevel(world, pos));
        bE.color = bE.fluid.getColor();
        bE.markDirty();
        return true;
    }

    //returns just water if theres a problem
    public static PotionContentsComponent removeFluid(World world, BlockPos pos) {
        if(world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).isEmpty()) return new PotionContentsComponent(Potions.WATER);
        ConcoctionCauldronBlockEntity bE = world.getBlockEntity(pos, BlockEntityTypes.CONCOCTION_CAULDRON).get();
        PotionContentsComponent potionContentsComponent = bE.fluid.removePotion((double) 1 / getFluidLevel(world, pos));
        if (!modifyFluidLevel(world, pos, -1)) {
            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
        }
        bE.markDirty();
        return potionContentsComponent;
    }

    public static int getFluidLevel(World world, BlockPos pos) {
        if(!world.getBlockState(pos).isOf(net.afternooncats.rebrewed.block.Blocks.CONCOCTION_CAULDRON)) return 0;
        return world.getBlockState(pos).get(ConcoctionCauldronBlock.LEVEL);
    }
    public static boolean setFluidLevel(World world, BlockPos pos, int level) {
        if (!world.getBlockState(pos).isOf(net.afternooncats.rebrewed.block.Blocks.CONCOCTION_CAULDRON)) return false;
        if (level < ConcoctionCauldronBlock.MIN_LEVEL || level > ConcoctionCauldronBlock.MAX_LEVEL) return false;
        return world.setBlockState(pos, world.getBlockState(pos).with(ConcoctionCauldronBlock.LEVEL, level));
    }
    public static boolean modifyFluidLevel(World world, BlockPos pos, int diff) {
        return setFluidLevel(world, pos, getFluidLevel(world, pos) + diff);
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
        super.writeNbt(nbt, registryLookup);

        nbt.put("fluid", this.fluid.writeNBT(registryLookup));
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        this.fluid = FluidInstance.fromNBT((NbtCompound) nbt.get("fluid"), registryLookup);

        markDirty();
    }
}