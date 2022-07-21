package be.ephys.fundamental.mixins;

import be.ephys.fundamental.slime_on_piston.PlantHeightModule;
import be.ephys.fundamental.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;


@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {

  @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
  private void randomTick$randomiseHeight(BlockState state, ServerLevel worldIn, BlockPos pos, Random random, CallbackInfo callbackInfo) {
    if (!PlantHeightModule.sugarCaneEnabled.get()) {
      return;
    }

    callbackInfo.cancel();

    BlockPos up = pos.above();
    if (!worldIn.isEmptyBlock(up)) {
      return;
    }

    SugarCaneBlock self = (SugarCaneBlock) (Object) this;

    int i;
    for(i = 1; worldIn.getBlockState(pos.below(i)).is(self); ++i) {
    }

    Random random2 = new Random(pos.asLong());
    int maxHeight = MathUtils.randomIntInclusive(random2, PlantHeightModule.sugarCaneMin.get(), PlantHeightModule.sugarCaneMax.get());

    if (i < maxHeight) {
      int j = state.getValue(SugarCaneBlock.AGE);
      if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true)) {
        if (j == 15) {
          worldIn.setBlockAndUpdate(up, self.defaultBlockState());
          worldIn.setBlock(pos, state.setValue(SugarCaneBlock.AGE, 0), 4);
        } else {
          worldIn.setBlock(pos, state.setValue(SugarCaneBlock.AGE, j + 1), 4);
        }
      }
    }
  }
}
