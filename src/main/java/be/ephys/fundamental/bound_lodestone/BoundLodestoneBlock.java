package be.ephys.fundamental.bound_lodestone;

import be.ephys.fundamental.Mod;
import be.ephys.fundamental.named_lodestone.LodestoneCompassUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class BoundLodestoneBlock extends BaseEntityBlock {
  public static final BooleanProperty BOUND = BooleanProperty.create("bound");

  public BoundLodestoneBlock(Properties properties) {
    super(properties);

    this.registerDefaultState(this.defaultBlockState().setValue(BOUND, false));
  }

  public static void unbind(Level world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);

    if (!state.is(BoundLodestoneModule.BOUND_LODESTONE.get())) {
      return;
    }

    world.setBlockAndUpdate(pos, state.setValue(BOUND, false));
  }

  public static BlockPos readCompassLodestonePos(CompoundTag nbt) {
    if (!nbt.contains("LodestonePos")) {
      return null;
    }

    return NbtUtils.readBlockPos(nbt.getCompound("LodestonePos"));
  }

  public static <T extends BlockEntity> T getBlockEntity(Class<T> teClass, Level world, BlockPos pos) {
    BlockEntity te = world.getBlockEntity(pos);

    if (te == null) {
      return null;
    }

    if (!teClass.isInstance(te)) {
      return null;
    }

    return teClass.cast(te);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BOUND);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new BoundLodestoneBlockEntity(pos, state);
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack heldItem = player.getItemInHand(hand);

    if (!heldItem.getItem().equals(Items.COMPASS)) {
      return super.use(state, world, pos, player, hand, hit);
    }

    if (!world.isClientSide()) {
      BoundLodestoneBlockEntity te = getBlockEntity(BoundLodestoneBlockEntity.class, world, pos);

      if (te == null) {
        return super.use(state, world, pos, player, hand, hit);
      }

      if (!te.isBound()) {
        if (!this.attemptBindLodestone(te, heldItem, player, world)) {
          return super.use(state, world, pos, player, hand, hit);
        }

        world.setBlockAndUpdate(pos, state.setValue(BOUND, true));
      } else {
        if (!this.attemptBindCompass(te, heldItem, player, world)) {
          return super.use(state, world, pos, player, hand, hit);
        }
      }
    }

    // TODO play another sound for binding the lodestone itself
    world.playSound(null, pos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);

    return InteractionResult.SUCCESS;
  }

  private boolean attemptBindLodestone(BoundLodestoneBlockEntity te, ItemStack heldItem, Player player, Level world) {
    CompoundTag itemTag = heldItem.getTag();
    //                                 .isLodestoneCompass
    if (itemTag == null || !CompassItem.isLodestoneCompass(heldItem)) {
      player.displayClientMessage(new TranslatableComponent("messages.fundamental.not_a_lodestone_compass"), true);

      return false;
    }

    // get Lodestone dimension
    ResourceKey<Level> lodestoneDim = CompassItem.getLodestoneDimension(itemTag).get();
    BlockPos targetPos = readCompassLodestonePos(itemTag);

    if (targetPos == null || !world.dimension().equals(lodestoneDim)) {
      player.displayClientMessage(new TranslatableComponent("messages.fundamental.your_lodestone_is_in_another_castle"), true);

      return false;
    }

    te.bindTo(targetPos);

    return true;
  }

  private boolean attemptBindCompass(BoundLodestoneBlockEntity te, ItemStack heldItem, Player player, Level world) {
    BlockPos targetPos = te.getTargetLodestonePos();
    BlockPos boundLoPos = te.getBlockPos();
    ResourceKey<Level> dim = world.dimension();

    // TODO: set display name if right clicking a sign
    // TODO: support right clicking a sign on a bound lodestone

    Abilities playerAbilities = player.getAbilities();

    boolean flag = !playerAbilities.instabuild && heldItem.getCount() == 1;
    if (flag) {
      this.addLodestoneNbt(dim, world, boundLoPos, targetPos, heldItem.getOrCreateTag());
    } else {
      ItemStack boundCompass = new ItemStack(Items.COMPASS, 1);
      CompoundTag tag = heldItem.hasTag() ? heldItem.getTag().copy() : new CompoundTag();
      boundCompass.setTag(tag);
      if (!playerAbilities.instabuild) {
        heldItem.shrink(1);
      }

      this.addLodestoneNbt(dim, world, boundLoPos, targetPos, tag);
      if (!player.getInventory().add(boundCompass)) {
        player.drop(boundCompass, false);
      }
    }

    return true;
  }

  private void addLodestoneNbt(ResourceKey<Level> lodestoneDim, Level world, BlockPos boundLoPos, BlockPos targetLodestonePos, CompoundTag nbt) {
    nbt.put("LodestonePos", NbtUtils.writeBlockPos(targetLodestonePos));
    Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, lodestoneDim).resultOrPartial(Mod.LOGGER::error).ifPresent((dimId) -> {
      nbt.put("LodestoneDimension", dimId);
    });
    nbt.putBoolean("LodestoneTracked", true);

    // use name of the Bound Lodestone
    Component name = LodestoneCompassUtils.getSignName(world, boundLoPos);
    if (name == null) {
      // use name of the Target Lodestone
      name = LodestoneCompassUtils.getSignName(world, targetLodestonePos);
    }

    LodestoneCompassUtils.setLodestoneName(nbt, name);
  }

  @Override
  public RenderShape getRenderShape(BlockState p_56794_) {
    return RenderShape.MODEL;
  }

  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> entityType) {
    return createTickerHelper(
      entityType,
      BoundLodestoneModule.BOUND_LODESTONE_TE_TYPE.get(),
      BoundLodestoneBlockEntity::tick
    );
  }
}
