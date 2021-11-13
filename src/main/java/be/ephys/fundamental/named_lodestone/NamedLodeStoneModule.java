package be.ephys.fundamental.named_lodestone;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.bound_lodestone.BoundLodestoneModule;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSignBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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

    World world = event.getWorld();
    BlockPos signPos = event.getPos();
    BlockState clickedSign = world.getBlockState(signPos);

    if (!(clickedSign.getBlock() instanceof WallSignBlock)) {
      return;
    }

    if (event.getSide() != LogicalSide.SERVER) {
      return;
    }

    Direction signDirection = clickedSign.get(WallSignBlock.FACING);
    BlockPos lodestonePos = signPos.offset(signDirection.getOpposite());
    BlockState attachedLodestone = world.getBlockState(lodestonePos);

    if (!attachedLodestone.isIn(Blocks.LODESTONE)
      && !attachedLodestone.isIn(BoundLodestoneModule.BOUND_LODESTONE.get())) {
      return;
    }

    rightClick(world, lodestonePos, event.getPlayer(), itemStack, event.getHand(), signDirection);
    event.setCancellationResult(ActionResultType.SUCCESS);
  }

  private static void rightClick(World world, BlockPos pos, PlayerEntity player, ItemStack itemStack, Hand hand, Direction facingOpposite) {
    if (hand != Hand.MAIN_HAND) {
      return;
    }

    BlockState attachedState = world.getBlockState(pos);

    BlockState stateDown = world.getBlockState(pos.down());
    BlockRayTraceResult rayTrace = new BlockRayTraceResult(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), facingOpposite, pos, false);
    ActionResultType result = itemStack.getItem().onItemUse(new ItemUseContext(player, hand, rayTrace));

    if (result == ActionResultType.PASS) {
      if (!world.isAirBlock(pos.down()) && attachedState.getBlock().isAir(attachedState, world, pos)) {
        stateDown.getBlock().onBlockActivated(attachedState, world, pos.down(), player, hand, rayTrace);
      } else if (!attachedState.getBlock().isAir(attachedState, world, pos)) {
        attachedState.getBlock().onBlockActivated(attachedState, world, pos, player, hand, rayTrace);
      }
    }
  }
}
