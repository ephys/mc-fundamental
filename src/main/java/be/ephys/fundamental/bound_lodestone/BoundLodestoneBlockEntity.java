package be.ephys.fundamental.bound_lodestone;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BoundLodestoneBlockEntity extends BlockEntity {
  private BlockPos boundTo;

  public BoundLodestoneBlockEntity(BlockPos pos, BlockState blockState) {
    super(BoundLodestoneModule.BOUND_LODESTONE_TE_TYPE.get(), pos, blockState);
  }

  @Override
  protected void saveAdditional(CompoundTag compound) {
    if (boundTo != null) {
      compound.put("LodestonePos", NbtUtils.writeBlockPos(boundTo));
    }

    super.saveAdditional(compound);
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);

    if (nbt.contains("LodestonePos")) {
      boundTo = NbtUtils.readBlockPos(nbt.getCompound("LodestonePos"));
    } else {
      boundTo = null;
    }
  }

  public void unbind() {
    this.boundTo = null;
    this.setChanged();
  }

  public boolean isBound() {
    return boundTo != null;
  }

  public void bindTo(BlockPos pos) {
    this.boundTo = pos;
    this.setChanged();
  }

  public BlockPos getTargetLodestonePos() {
    return this.boundTo;
  }


  public static void tick(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, BoundLodestoneBlockEntity blockEntity) {
    blockEntity.checkBound();
  }

  private void checkBound() {
    if (!(this.getLevel() instanceof ServerLevel)) {
      return;
    }

    if (this.boundTo == null) {
      return;
    }

    if (!targetLodestoneExists((ServerLevel) this.getLevel(), boundTo)) {
      this.unbind();

      BoundLodestoneBlock.unbind(this.getLevel(), getBlockPos());
    }
  }

  private static boolean targetLodestoneExists(ServerLevel world, BlockPos pos) {
    return world.getPoiManager().existsAtPosition(PoiType.LODESTONE, pos);
  }
}
