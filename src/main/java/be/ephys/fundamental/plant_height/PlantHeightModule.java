package be.ephys.fundamental.plant_height;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeHooks;

import java.util.Random;

public class PlantHeightModule {
  @Config(
    name = "random_cactus_height.enabled",
    description = "Randomizes the height of cacti."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue cactusEnabled;

  @Config(
    name = "random_cactus_height.min",
    description = "Minimum height of a cactus (in blocks). Cacti will grow to at least this size."
  )
  @Config.IntDefault(2)
  public static ForgeConfigSpec.IntValue cactusMinHeight;

  @Config(
    name = "random_cactus_height.max",
    description = "Maximum height of a cactus (in blocks). Cacti will grow to at most this size."
  )
  @Config.IntDefault(4)
  public static ForgeConfigSpec.IntValue cactusMaxHeight;

  @Config(
    name = "random_sugar_cane_height.enabled",
    description = "Randomizes the height of sugar canes."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue sugarCaneEnabled;

  @Config(
    name = "random_sugar_cane_height.min",
    description = "Minimum height of sugar canes (in blocks). Sugar canes will grow to at least this size."
  )
  @Config.IntDefault(2)
  public static ForgeConfigSpec.IntValue sugarCaneMinHeight;

  @Config(
    name = "random_sugar_cane_height.max",
    description = "Maximum height of sugar canes (in blocks). Sugar canes will grow to at most this size."
  )
  @Config.IntDefault(4)
  public static ForgeConfigSpec.IntValue sugarCaneMaxHeight;

  @Config(
    name = "random_vine_height.enabled",
    description = "Randomizes the height of sugar canes."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue vineEnabled;

  @Config(
    name = "random_vine_height.min",
    description = "Minimum height of vines (in blocks). Vines will grow to at least this size."
  )
  @Config.IntDefault(4)
  public static ForgeConfigSpec.IntValue vineMinHeight;

  @Config(
    name = "random_vine_height.max",
    description = "Maximum height of vines (in blocks). Vines will grow to at most this size."
  )
  @Config.IntDefault(7)
  public static ForgeConfigSpec.IntValue vineMaxHeight;

  @Config(
    name = "random_vine_height.min_floor_distance",
    description = "At which distance from the floor will the vine stop growing (at a minimum)."
  )
  @Config.IntDefault(0)
  public static ForgeConfigSpec.IntValue vineMinFloorDistance;

  @Config(
    name = "random_vine_height.max_floor_Distance",
    description = "At which distance from the floor will the vine stop growing (at a maximum)."
  )
  @Config.IntDefault(2)
  public static ForgeConfigSpec.IntValue vineMaxFloorDistance;

  public static boolean growCactusOrSugarCane(BlockState blockState, Level level, BlockPos pos, Random random, boolean usingBonemeal) {
    BlockPos up = pos.above();
    if (!level.isEmptyBlock(up)) {
      return false;
    }

    Block block = blockState.getBlock();
    int minHeight = block == Blocks.CACTUS
      ? cactusEnabled.get() ? cactusMinHeight.get() : 3
      : sugarCaneEnabled.get() ? sugarCaneMinHeight.get() : 3;
    int maxHeight = block == Blocks.CACTUS
      ? cactusEnabled.get() ? cactusMaxHeight.get() : 3
      : sugarCaneEnabled.get() ? sugarCaneMaxHeight.get() : 3;

    int i;
    for (i = 1; level.getBlockState(pos.below(i)).is(block); ++i) {
    }

    Random posBasedRandom = new Random(pos.asLong());
    int maxHeightForPos = MathUtils.randomIntInclusive(posBasedRandom, minHeight, maxHeight);

    if (i >= maxHeightForPos) {
      return false;
    }

    int currentAge = blockState.getValue(BlockStateProperties.AGE_15);
    if (!ForgeHooks.onCropsGrowPre(level, up, blockState, true)) {
      return false;
    }

    if (!level.isClientSide()) {
      int ageBoost = usingBonemeal ? random.nextInt(1, 15) : 1;
      int newAge = currentAge + ageBoost;

      if (newAge > 15) {
        level.setBlockAndUpdate(up, block.defaultBlockState());
        BlockState blockstate = blockState.setValue(BlockStateProperties.AGE_15, 0);
        level.setBlock(pos, blockstate, 4);
        blockstate.neighborChanged(level, up, block, pos, false);
      } else {
        level.setBlock(pos, blockState.setValue(BlockStateProperties.AGE_15, newAge), 4);
      }
    }

    ForgeHooks.onCropsGrowPost(level, pos, blockState);

    return true;
  }
}
