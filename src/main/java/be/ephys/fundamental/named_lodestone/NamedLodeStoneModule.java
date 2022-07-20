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
   *
   * Makes signs on Lodestones & Bound Lodestones pass-through for right-clicks with a compass.
   */
  public static void onRightClickSignWithCompass(PlayerInteractEvent.RightClickBlock event) {
    ItemStack itemStack = event.getItemStack();

    if (itemStack.getItem() != Items.COMPASS) {
      return;
    }

    Level world = event.getWorld();
    BlockPos signPos = event.getPos();
    BlockState clickedSign = world.getBlockState(signPos);

    if (!(clickedSign.getBlock() instanceof WallSignBlock)) {
      return;
    }

    if (event.getSide() != LogicalSide.SERVER) {
      return;
    }

    Direction signDirection = clickedSign.getValue(WallSignBlock.FACING);
    BlockPos lodestonePos = signPos.relative(signDirection.getOpposite());
    BlockState attachedLodestone = world.getBlockState(lodestonePos);

    if (!attachedLodestone.is(Blocks.LODESTONE)
      && !attachedLodestone.is(BoundLodestoneModule.BOUND_LODESTONE.get())) {
      return;
    }

    rightClick(world, lodestonePos, event.getPlayer(), itemStack, event.getHand(), signDirection);
    event.setCancellationResult(InteractionResult.SUCCESS);
  }

  private static void rightClick(Level world, BlockPos pos, Player player, ItemStack itemStack, InteractionHand hand, Direction facingOpposite) {
    if (hand != InteractionHand.MAIN_HAND) {
      return;
    }

    BlockState attachedState = world.getBlockState(pos);

    BlockState stateDown = world.getBlockState(pos.below());
    BlockHitResult rayTrace = new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), facingOpposite, pos, false);
    InteractionResult result = itemStack.getItem().useOn(new UseOnContext(player, hand, rayTrace));

    if (result == InteractionResult.PASS) {
      if (!world.isEmptyBlock(pos.below()) && attachedState.isAir()) {
        stateDown.use(world, player, hand, rayTrace);
      } else if (!attachedState.isAir()) {
        attachedState.use(world, player, hand, rayTrace);
      }
    }
  }
}
