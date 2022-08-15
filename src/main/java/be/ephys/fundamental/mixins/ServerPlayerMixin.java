package be.ephys.fundamental.mixins;

import be.ephys.fundamental.BedTooFarFix;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

  /**
   * This makes beds always usable if they are within the player's reach distance.
   * No more "You may not sleep now; the bed is too far away"
   */
  @Inject(at = @At("HEAD"), method = "isReachableBedBlock", cancellable = true)
  private void isReachableBedBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    if (!BedTooFarFix.enabled.get()) {
      return;
    }

    cir.cancel();

    ServerPlayer player = (ServerPlayer) (Object) this;

    double d0 = player.getX() - ((double)pos.getX() + 0.5D);
    double d1 = player.getY() - ((double)pos.getY() + 0.5D) + 1.5D;
    double d2 = player.getZ() - ((double)pos.getZ() + 0.5D);
    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
    double dist = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue() + 1;
    dist *= dist;

    cir.setReturnValue(d3 <= dist);
  }
}
