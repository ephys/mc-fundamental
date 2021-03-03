package be.ephys.fundamental.bound_lodestone;

import be.ephys.fundamental.Mod;
import be.ephys.fundamental.named_lodestone.LodestoneCompassUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BoundLodestoneBlock extends Block {
  public static final BooleanProperty BOUND = BooleanProperty.create("bound");

  public BoundLodestoneBlock(Properties properties) {
    super(properties);

    this.setDefaultState(this.stateContainer
      .getBaseState().with(BOUND, false));
  }

  public static void unbind(World world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);

    if (!state.isIn(BoundLodestoneModule.BOUND_LODESTONE.get())) {
      return;
    }

    world.setBlockState(pos, state.with(BOUND, false));
  }

  public static BlockPos readCompassLodestonePos(CompoundNBT nbt) {
    if (!nbt.contains("LodestonePos")) {
      return null;
    }

    return NBTUtil.readBlockPos(nbt.getCompound("LodestonePos"));
  }

  public static <T extends TileEntity> T getTileEntity(Class<T> teClass, World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);

    if (te == null) {
      return null;
    }

    if (!teClass.isInstance(te)) {
      return null;
    }

    return teClass.cast(te);
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(BOUND);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new BoundLodestoneTileEntity();
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    ItemStack heldItem = player.getHeldItem(handIn);

    if (!heldItem.getItem().equals(Items.COMPASS)) {
      return super.onBlockActivated(state, world, pos, player, handIn, hit);
    }

    if (!world.isRemote) {
      BoundLodestoneTileEntity te = getTileEntity(BoundLodestoneTileEntity.class, world, pos);

      if (te == null) {
        return super.onBlockActivated(state, world, pos, player, handIn, hit);
      }

      if (!te.isBound()) {
        if (!this.attemptBindLodestone(te, heldItem, player, world)) {
          return super.onBlockActivated(state, world, pos, player, handIn, hit);
        }

        world.setBlockState(pos, state.with(BOUND, true));
      } else {
        if (!this.attemptBindCompass(te, heldItem, player, world)) {
          return super.onBlockActivated(state, world, pos, player, handIn, hit);
        }
      }
    }

    // TODO play another sound for binding the lodestone itself
    world.playSound(null, pos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);

    return ActionResultType.SUCCESS;
  }

  private boolean attemptBindLodestone(BoundLodestoneTileEntity te, ItemStack heldItem, PlayerEntity player, World world) {
    CompoundNBT itemTag = heldItem.getTag();
    //                                 .isLodestoneCompass
    if (itemTag == null || !CompassItem.func_234670_d_(heldItem)) {
      player.sendStatusMessage(new TranslationTextComponent("messages.fundamental.not_a_lodestone_compass"), true);

      return false;
    }

    // get Lodestone dimension
    RegistryKey<World> lodestoneDim = CompassItem.func_234667_a_(itemTag).get();
    BlockPos targetPos = readCompassLodestonePos(itemTag);

    if (targetPos == null || world.getDimensionKey() != lodestoneDim) {
      player.sendStatusMessage(new TranslationTextComponent("messages.fundamental.your_lodestone_is_in_another_castle"), true);

      return false;
    }

    te.bindTo(targetPos);

    return true;
  }

  private boolean attemptBindCompass(BoundLodestoneTileEntity te, ItemStack heldItem, PlayerEntity player, World world) {
    BlockPos targetPos = te.getTargetLodestonePos();
    BlockPos boundLoPos = te.getPos();
    RegistryKey<World> dim = world.getDimensionKey();

    // TODO: set display name if right clicking a sign
    // TODO: support right clicking a sign on a bound lodestone

    boolean flag = !player.abilities.isCreativeMode && heldItem.getCount() == 1;
    if (flag) {
      this.addLodestoneNbt(dim, world, boundLoPos, targetPos, heldItem.getOrCreateTag());
    } else {
      ItemStack boundCompass = new ItemStack(Items.COMPASS, 1);
      CompoundNBT compoundnbt = heldItem.hasTag() ? heldItem.getTag().copy() : new CompoundNBT();
      boundCompass.setTag(compoundnbt);
      if (!player.abilities.isCreativeMode) {
        heldItem.shrink(1);
      }

      this.addLodestoneNbt(dim, world, boundLoPos, targetPos, compoundnbt);
      if (!player.inventory.addItemStackToInventory(boundCompass)) {
        player.dropItem(boundCompass, false);
      }
    }

    return true;
  }

  private void addLodestoneNbt(RegistryKey<World> lodestoneDim, World world, BlockPos boundLoPos, BlockPos targetLodestonePos, CompoundNBT nbt) {
    nbt.put("LodestonePos", NBTUtil.writeBlockPos(targetLodestonePos));
    World.CODEC.encodeStart(NBTDynamicOps.INSTANCE, lodestoneDim).resultOrPartial(Mod.LOGGER::error).ifPresent((dimId) -> {
      nbt.put("LodestoneDimension", dimId);
    });
    nbt.putBoolean("LodestoneTracked", true);

    // use name of the Bound Lodestone
    ITextComponent name = LodestoneCompassUtils.getSignName(world, boundLoPos);
    if (name == null) {
      // use name of the Target Lodestone
      name = LodestoneCompassUtils.getSignName(world, targetLodestonePos);
    }

    LodestoneCompassUtils.setLodestoneName(nbt, name);
  }
}
