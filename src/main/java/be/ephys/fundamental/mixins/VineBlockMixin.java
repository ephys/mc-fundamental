package be.ephys.fundamental.mixins;

import be.ephys.fundamental.plant_height.PlantHeightModule;
import be.ephys.fundamental.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(VineBlock.class)
public class VineBlockMixin {

  @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
  private void randomTick$handleVineAge(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo callbackInfo) {
    if (!PlantHeightModule.vineEnabled.get()) {
      return;
    }

    if (cantGrowDown(level, pos)) {
      return;
    }

    // seeded random so we always get the same value for the same block coords
    // easier than adding an age block property
    Random random2 = new Random(pos.asLong());
    int maxHeight = MathUtils.randomIntInclusive(random2, PlantHeightModule.vineMinHeight.get(), PlantHeightModule.vineMaxHeight.get());
    if (!isVineHeightAtMost(maxHeight, level, pos)) {
      callbackInfo.cancel();
      return;
    }

    int minAirSpace = MathUtils.randomIntInclusive(random2, PlantHeightModule.vineMinFloorDistance.get(), PlantHeightModule.vineMaxFloorDistance.get());
    if (!isBottomFreeSpaceAtLeast(minAirSpace, level, pos.below())) {
      callbackInfo.cancel();
    }
  }

  private static boolean isVineHeightAtMost(int maxHeight, ServerLevel level, BlockPos pos) {
    int height = 0;

    while (level.getBlockState(pos).getBlock() == Blocks.VINE) {
      height += 1;
      pos = pos.above();

      if (height >= maxHeight) {
        return false;
      }
    }

    return true;
  }

  private static boolean isBottomFreeSpaceAtLeast(int minHeight, ServerLevel level, BlockPos pos) {
    int height = 0;

    while (level.getBlockState(pos).getBlock() == Blocks.AIR) {
      height += 1;
      pos = pos.below();

      if (height > minHeight) {
        return true;
      }
    }

    return false;
  }

  private static boolean cantGrowDown(ServerLevel level, BlockPos pos) {
    return level.getBlockState(pos.below()).getBlock() != Blocks.AIR;
  }
}
