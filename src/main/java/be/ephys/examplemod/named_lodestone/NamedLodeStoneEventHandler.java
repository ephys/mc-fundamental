package be.ephys.examplemod.named_lodestone;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSignBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class NamedLodeStoneEventHandler {
  /**
   * Part of the Sign-on-a-lodestone renames your compass.
   *
   * Makes right clicking a sign with a compass right click the lodestone behind it instead.
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

    Direction signDirection = clickedSign.get(WallSignBlock.FACING);
    BlockPos lodestonePos = signPos.offset(signDirection.getOpposite());
    BlockState attachedLodestone = world.getBlockState(lodestonePos);

    //                             .LODESTONE
    if (!attachedLodestone.isIn(Blocks.field_235405_no_)) {
      return;
    }

    // right click lodestone instead of sign
    // alternatively: use rayTrace like LilyPadItem?
    BlockRayTraceResult blockRayTraceResult = new BlockRayTraceResult(Vector3d.ZERO, signDirection, lodestonePos, false);
    ActionResultType result = itemStack.getItem().onItemUse(new ItemUseContext(event.getPlayer(), event.getHand(), blockRayTraceResult));
    event.setCancellationResult(result);
    event.setCanceled(true);
  }
}
