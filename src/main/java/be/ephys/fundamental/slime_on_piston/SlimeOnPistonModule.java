package be.ephys.fundamental.slime_on_piston;

import be.ephys.cookiecore.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
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

  public static final TagKey<Item> slimeballsTag = ItemTags.create(new ResourceLocation("forge", "slimeballs"));
  public static final TagKey<Item> axesTag = ItemTags.create(new ResourceLocation("forge", "tools/axes"));

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
    if (!usedItemStack.is(slimeballsTag)) {
      return;
    }

    Level world = event.getWorld();
    BlockPos pos = event.getPos();
    BlockState targetedBlockState = world.getBlockState(pos);

    if (!(targetedBlockState.getBlock() == Blocks.PISTON && !targetedBlockState.getValue(PistonBaseBlock.EXTENDED))
      && !(targetedBlockState.getBlock() == Blocks.PISTON_HEAD && targetedBlockState.getValue(PistonHeadBlock.TYPE) == PistonType.DEFAULT)) {
      return;
    }

    Direction blockFace = targetedBlockState.getValue(DirectionalBlock.FACING);
    if (event.getFace() != blockFace) {
      return;
    }

    if (!world.isClientSide()) {
      // turn into sticky piston
      if (targetedBlockState.getBlock() == Blocks.PISTON) {
        BlockState newBlockState = Blocks.STICKY_PISTON.withPropertiesOf(targetedBlockState);

        world.setBlockAndUpdate(pos, newBlockState);
      } else {
        BlockState newBlockState = targetedBlockState.setValue(PistonHeadBlock.TYPE, PistonType.STICKY);

        BlockPos pistonBasePos = pos.relative(blockFace.getOpposite());
        BlockState pistonBaseBlockState = world.getBlockState(pistonBasePos);

        BlockState newBaseBlockState = Blocks.STICKY_PISTON.withPropertiesOf(pistonBaseBlockState);

        world.setBlockAndUpdate(pos, newBlockState);
        world.setBlockAndUpdate(pistonBasePos, newBaseBlockState);
      }

      Player player = event.getPlayer();
      if (!player.getAbilities().instabuild) {
        usedItemStack.shrink(1);
      }
    }

    world.playSound(event.getPlayer(), pos.getX(), pos.getY(), pos.getZ(), SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 1f, 1f);

    event.setCanceled(true);
    event.setCancellationResult(world.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME);
  }

  public static void axeThatPiston(PlayerInteractEvent.RightClickBlock event) {
    ItemStack usedItemStack = event.getItemStack();
    if (!usedItemStack.is(axesTag)) {
      return;
    }

    Level world = event.getWorld();
    BlockPos pos = event.getPos();
    BlockState targetedBlockState = world.getBlockState(pos);

    if (!(targetedBlockState.getBlock() == Blocks.STICKY_PISTON && !targetedBlockState.getValue(PistonBaseBlock.EXTENDED))
      && !(targetedBlockState.getBlock() == Blocks.PISTON_HEAD && targetedBlockState.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY)) {
      return;
    }

    Direction blockFace = targetedBlockState.getValue(DirectionalBlock.FACING);
    if (event.getFace() != blockFace) {
      return;
    }

    Player player = event.getPlayer();

    if (!world.isClientSide()) {
      // turn into sticky piston
      if (targetedBlockState.getBlock() == Blocks.STICKY_PISTON) {
        BlockState newBlockState = Blocks.PISTON.withPropertiesOf(targetedBlockState);

        world.setBlockAndUpdate(pos, newBlockState);
      } else {
        BlockState newBlockState = targetedBlockState.setValue(PistonHeadBlock.TYPE, PistonType.DEFAULT);

        BlockPos pistonBasePos = pos.relative(blockFace.getOpposite());
        BlockState pistonBaseBlockState = world.getBlockState(pistonBasePos);

        BlockState newBaseBlockState = Blocks.PISTON.withPropertiesOf(pistonBaseBlockState);

        world.setBlockAndUpdate(pos, newBlockState);
        world.setBlockAndUpdate(pistonBasePos, newBaseBlockState);
      }

      if (!player.getAbilities().instabuild) {
        usedItemStack.hurtAndBreak(1, player, (p) -> {
          p.broadcastBreakEvent(event.getHand());
        });
      }
    }

    world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.SLIME_BLOCK_BREAK, SoundSource.BLOCKS, 1f, 1f);

    event.setCanceled(true);
    event.setCancellationResult(world.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME);
  }
}
