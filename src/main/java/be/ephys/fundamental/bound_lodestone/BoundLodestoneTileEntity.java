package be.ephys.fundamental.bound_lodestone;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class BoundLodestoneTileEntity extends TileEntity implements ITickableTileEntity {
  private BlockPos boundTo;

  public BoundLodestoneTileEntity() {
    super(BoundLodestoneModule.boundLodestoneTeType);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    if (boundTo != null) {
      compound.put("LodestonePos", NBTUtil.writeBlockPos(boundTo));
    }

    return super.write(compound);
  }

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);

    if (nbt.contains("LodestonePos")) {
      boundTo = NBTUtil.readBlockPos(nbt.getCompound("LodestonePos"));
    } else {
      boundTo = null;
    }
  }

  public void unbind() {
    this.boundTo = null;
    this.markDirty();
  }

  public boolean isBound() {
    return boundTo != null;
  }

  public void bindTo(BlockPos pos) {
    this.boundTo = pos;
    this.markDirty();
  }

  public BlockPos getTargetLodestonePos() {
    return this.boundTo;
  }


  @Override
  public void tick() {
    this.checkBound();
  }

  private void checkBound() {
    if (!(this.getWorld() instanceof ServerWorld)) {
      return;
    }

    if (this.boundTo == null) {
      return;
    }

    if (!targetLodestoneExists((ServerWorld) this.getWorld(), boundTo)) {
      this.unbind();

      BoundLodestoneBlock.unbind(this.getWorld(), getPos());
    }
  }

  private static boolean targetLodestoneExists(ServerWorld world, BlockPos pos) {
    return world.getPointOfInterestManager().hasTypeAtPosition(PointOfInterestType.LODESTONE, pos);
  }
}
