package be.ephys.fundamental.slime_on_piston;

import be.ephys.fundamental.helpers.BlockHelper;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SlimeOnPistonFeature {

  public static final Tags.IOptionalNamedTag<Item> slimeballsTag = ItemTags.createOptional(new ResourceLocation("forge", "slimeballs"));

  @SubscribeEvent
  public void slimeThatPiston(PlayerInteractEvent.RightClickBlock event) {
    ItemStack usedItemStack = event.getItemStack();
    if (usedItemStack.getItem() != Items.SLIME_BALL && !usedItemStack.getItem().isIn(slimeballsTag)) {
      return;
    }

    World world = event.getWorld();
    BlockPos pos = event.getPos();
    BlockState targetedBlockState = world.getBlockState(pos);

    if (!(targetedBlockState.getBlock() == Blocks.PISTON && !targetedBlockState.get(PistonBlock.EXTENDED))
      && !(targetedBlockState.getBlock() == Blocks.PISTON_HEAD && targetedBlockState.get(PistonHeadBlock.TYPE) == PistonType.DEFAULT)) {
      return;
    }

    Direction blockFace = targetedBlockState.get(DirectionalBlock.FACING);

    if (event.getFace() != blockFace) {
      return;
    }

    if (!world.isRemote) {
      // turn into sticky piston
      if (targetedBlockState.getBlock() == Blocks.PISTON) {
        BlockState newBlockState = BlockHelper.assignBlockState(Blocks.STICKY_PISTON.getDefaultState(), targetedBlockState);

        world.setBlockState(pos, newBlockState);
      } else {
        BlockState newBlockState = targetedBlockState.with(PistonHeadBlock.TYPE, PistonType.STICKY);

        BlockPos pistonBasePos = pos.offset(blockFace.getOpposite());
        BlockState pistonBaseBlockState = world.getBlockState(pistonBasePos);

        BlockState newBaseBlockState = BlockHelper.assignBlockState(Blocks.STICKY_PISTON.getDefaultState(), pistonBaseBlockState);

        world.setBlockState(pos, newBlockState);
        world.setBlockState(pistonBasePos, newBaseBlockState);
      }

      PlayerEntity player = event.getPlayer();
      if (!player.abilities.isCreativeMode) {
        usedItemStack.shrink(1);
      }
    }

    world.playSound(event.getPlayer(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1f, 1f);

    event.setCanceled(true);
    event.setCancellationResult(world.isRemote ? ActionResultType.SUCCESS : ActionResultType.CONSUME);
  }
}
