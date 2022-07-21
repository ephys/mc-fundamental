package be.ephys.fundamental.mixins;

import be.ephys.fundamental.plant_height.PlantHeightModule;
import be.ephys.fundamental.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CactusBlock.class)
public class CactusBlockMixin {
  @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
  private void randomTick$randomiseHeight(BlockState state, ServerLevel worldIn, BlockPos pos, Random random, CallbackInfo callbackInfo) {
    if (!PlantHeightModule.cactusEnabled.get()) {
      return;
    }

    callbackInfo.cancel();

    BlockPos up = pos.above();
    if (!worldIn.isEmptyBlock(up)) {
      return;
    }

    CactusBlock self = (CactusBlock) (Object) this;

    int i;
    for(i = 1; worldIn.getBlockState(pos.below(i)).is(self); ++i) {
    }

    Random random2 = new Random(pos.asLong());
    int maxHeight = MathUtils.randomIntInclusive(random2, PlantHeightModule.cactusMin.get(), PlantHeightModule.cactusMax.get());

    if (i < maxHeight) {
      int j = state.getValue(CactusBlock.AGE);
      if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, up, state, true)) {
        if (j == 15) {
          worldIn.setBlockAndUpdate(up, self.defaultBlockState());
          BlockState blockstate = state.setValue(CactusBlock.AGE, 0);
          worldIn.setBlock(pos, blockstate, 4);
          blockstate.neighborChanged(worldIn, up, self, pos, false);
        } else {
          worldIn.setBlock(pos, state.setValue(CactusBlock.AGE, j + 1), 4);
        }
        net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
      }
    }
  }
}
