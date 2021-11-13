package be.ephys.fundamental.slime_on_piston;

import be.ephys.cookiecore.config.Config;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = Mod.EventBusSubscriber.Bus.MOD
)
public class SlimeOnPistonModule {

  @Config(name = "use_slime_on_piston", description = "Use slime on a piston face to turn it into a sticky piston")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue addSlimeEnabled;

  @Config(name = "use_axe_on_sticky_piston", description = "Use an axe on a sticky piston face to turn it into a regular piston")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue removeSlimeEnabled;

  public static final Tags.IOptionalNamedTag<Item> slimeballsTag = ItemTags.createOptional(new ResourceLocation("forge", "slimeballs"));
  public static final Tags.IOptionalNamedTag<Item> axesTag = ItemTags.createOptional(new ResourceLocation("forge", "tools/axes"));

  @SubscribeEvent
  public static void onCommonSetup(FMLCommonSetupEvent event) {
    if (addSlimeEnabled.get()) {
      MinecraftForge.EVENT_BUS.addListener(SlimeOnPistonModule::slimeThatPiston);
    }

    if (removeSlimeEnabled.get()) {
      MinecraftForge.EVENT_BUS.addListener(SlimeOnPistonModule::axeThatPiston);
    }
  }

  public static void slimeThatPiston(PlayerInteractEvent.RightClickBlock event) {
    ItemStack usedItemStack = event.getItemStack();
    if (!usedItemStack.getItem().isIn(slimeballsTag)) {
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

  public static void axeThatPiston(PlayerInteractEvent.RightClickBlock event) {
    ItemStack usedItemStack = event.getItemStack();
    if (!usedItemStack.getItem().isIn(axesTag)) {
      return;
    }

    World world = event.getWorld();
    BlockPos pos = event.getPos();
    BlockState targetedBlockState = world.getBlockState(pos);

    if (!(targetedBlockState.getBlock() == Blocks.STICKY_PISTON && !targetedBlockState.get(PistonBlock.EXTENDED))
      && !(targetedBlockState.getBlock() == Blocks.PISTON_HEAD && targetedBlockState.get(PistonHeadBlock.TYPE) == PistonType.STICKY)) {
      return;
    }

    Direction blockFace = targetedBlockState.get(DirectionalBlock.FACING);
    if (event.getFace() != blockFace) {
      return;
    }

    PlayerEntity player = event.getPlayer();

    if (!world.isRemote) {
      // turn into sticky piston
      if (targetedBlockState.getBlock() == Blocks.STICKY_PISTON) {
        BlockState newBlockState = BlockHelper.assignBlockState(Blocks.PISTON.getDefaultState(), targetedBlockState);

        world.setBlockState(pos, newBlockState);
      } else {
        BlockState newBlockState = targetedBlockState.with(PistonHeadBlock.TYPE, PistonType.DEFAULT);

        BlockPos pistonBasePos = pos.offset(blockFace.getOpposite());
        BlockState pistonBaseBlockState = world.getBlockState(pistonBasePos);

        BlockState newBaseBlockState = BlockHelper.assignBlockState(Blocks.PISTON.getDefaultState(), pistonBaseBlockState);

        world.setBlockState(pos, newBlockState);
        world.setBlockState(pistonBasePos, newBaseBlockState);
      }

      if (!player.abilities.isCreativeMode) {
        usedItemStack.damageItem(1, player, (p_220043_1_) -> {
          p_220043_1_.sendBreakAnimation(event.getHand());
        });
      }
    }

    world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundCategory.BLOCKS, 1f, 1f);

    event.setCanceled(true);
    event.setCancellationResult(world.isRemote ? ActionResultType.SUCCESS : ActionResultType.CONSUME);
  }
}
