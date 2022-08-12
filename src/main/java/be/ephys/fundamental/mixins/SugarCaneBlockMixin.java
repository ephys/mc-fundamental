package be.ephys.fundamental.mixins;

import be.ephys.fundamental.plant_height.PlantHeightModule;
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

    PlantHeightModule.growCactusOrSugarCane(state, worldIn, pos, random, false);
  }
}
