package be.ephys.fundamental.bonemeal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Random;

@Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = Mod.EventBusSubscriber.Bus.MOD
)
public class BonemealModule {
  private static final Random random = new Random();

  @SubscribeEvent
  public static void onCommonSetup(FMLCommonSetupEvent t) {
    MinecraftForge.EVENT_BUS.addListener(BonemealModule::onBoneMealUse);
  }

  public static void onBoneMealUse(BonemealEvent event) {
    BlockState dirtBlock = event.getBlock();

    if (!dirtBlock.is(Blocks.LILY_PAD)) {
      return;
    }

    BlockPos lilyPos = event.getPos();
    Level level = event.getWorld();

    BlockPos spawnPos = findSuitableSpawnPos(lilyPos, level);
    if (spawnPos == null) {
      return;
    }

    if (!level.isClientSide()) {
      level.setBlockAndUpdate(spawnPos, Blocks.LILY_PAD.defaultBlockState());
    }

    event.setResult(Event.Result.ALLOW);
  }

  private static BlockPos findSuitableSpawnPos(BlockPos from, Level level) {
    WaterlilyBlock lilyPadBlock = (WaterlilyBlock) Blocks.LILY_PAD;

    int existingLilyPads = 0;
    BlockPos[] validSlots = new BlockPos[25];
    int validSlotCount = 0;

    for (int x = 0; x < 5; ++x) {
      for (int z = 0; z < 5; z++) {
        BlockPos newPos = from.offset(x - 2, 0, z - 2);
        BlockState newLilyBlockState = level.getBlockState(newPos);
        if (newLilyBlockState.is(Blocks.LILY_PAD)) {
          existingLilyPads++;
          // no more than 14 lily pads in a 5x5 square will grow
          if (existingLilyPads >= 12) {
            return null;
          }

          continue;
        }

        if (!newLilyBlockState.isAir()) {
          continue;
        }

        BlockPos newWaterPos = newPos.below();
        BlockState newWaterBlockState = level.getBlockState(newWaterPos);

        if (lilyPadBlock.mayPlaceOn(newWaterBlockState, level, newWaterPos)) {
          validSlots[validSlotCount++] = newPos;
        }
      }
    }

    if (validSlotCount == 0) {
      return null;
    }

    return validSlots[random.nextInt(0, validSlotCount)];
  }
}
