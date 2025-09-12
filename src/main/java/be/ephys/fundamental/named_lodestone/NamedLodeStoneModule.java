package be.ephys.fundamental.named_lodestone;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.bound_lodestone.BoundLodestoneModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD
)
public class NamedLodeStoneModule {
  @Config(
    name = "lodestone.rename_compass_on_use",
    description = "If a sign is placed on a lodestone (or bound lodestone), using a compass on that lodestone will rename the compass to the text on the sign (unless the compass was already named)."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue enabled;

  @SubscribeEvent
  public static void onCommonSetup(FMLCommonSetupEvent t) {
    if (enabled.get()) {
      MinecraftForge.EVENT_BUS.addListener(NamedLodeStoneModule::onRightClickSignWithCompass);
    }
  }

  /**
   * Part of the Sign-on-a-lodestone renames your compass.
   * <p>
   * Makes signs on Lodestones & Bound Lodestones pass-through for right-clicks with a compass.
   */
  public static void onRightClickSignWithCompass(PlayerInteractEvent.RightClickBlock event) {
    ItemStack itemStack = event.getItemStack();

    if (itemStack.getItem() != Items.COMPASS) {
      return;
    }

    Level level = event.getLevel();
    BlockPos signPos = event.getPos();
    BlockState clickedSign = level.getBlockState(signPos);

    if (!(clickedSign.getBlock() instanceof WallSignBlock)) {
      return;
    }

    BlockEntity blockEntity = level.getBlockEntity(signPos);
    if (!(blockEntity instanceof SignBlockEntity signBlockEntity)) {
      return;
    }

    if (!signBlockEntity.isWaxed()) {
      return;
    }

    if (event.getSide() != LogicalSide.SERVER) {
      return;
    }

    Direction signDirection = clickedSign.getValue(WallSignBlock.FACING);
    BlockPos lodestonePos = signPos.relative(signDirection.getOpposite());
    BlockState attachedLodestone = level.getBlockState(lodestonePos);

    if (!attachedLodestone.is(Blocks.LODESTONE)
      && !attachedLodestone.is(BoundLodestoneModule.BOUND_LODESTONE.get())) {
      return;
    }

    rightClick(level, lodestonePos, event.getEntity(), itemStack, event.getHand(), signDirection);
    event.setCancellationResult(InteractionResult.SUCCESS);
  }

  private static void rightClick(Level level, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand, Direction facingOpposite) {
    if (hand != InteractionHand.MAIN_HAND) {
      return;
    }

    BlockState attachedState = level.getBlockState(pos);

    BlockState stateDown = level.getBlockState(pos.below());
    BlockHitResult rayTrace = new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), facingOpposite, pos, false);
    InteractionResult result = itemStack.getItem().useOn(new UseOnContext(player, hand, rayTrace));

    if (result == InteractionResult.PASS) {
      if (!level.isEmptyBlock(pos.below()) && attachedState.isAir()) {
        stateDown.use(level, player, hand, rayTrace);
      } else if (!attachedState.isAir()) {
        attachedState.use(level, player, hand, rayTrace);
      }
    }
  }
}
