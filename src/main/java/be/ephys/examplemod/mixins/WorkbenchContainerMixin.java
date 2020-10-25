package be.ephys.examplemod.mixins;

import be.ephys.examplemod.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IWorldPosCallable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorkbenchContainer.class)
public class WorkbenchContainerMixin {

  @Shadow
  @Final
  private IWorldPosCallable worldPosCallable;

  @Inject(method = "canInteractWith", at = @At("RETURN"), cancellable = true)
  public void canInteractWith$useTagSystem(PlayerEntity playerIn, CallbackInfoReturnable<Boolean> cir) {
    boolean canInteractWith = cir.getReturnValue();

    if (canInteractWith) {
      return;
    }

    cir.setReturnValue(isWithinUsableDistance(this.worldPosCallable, playerIn, ModRegistry.CRAFTING_TABLE_TAG_WRAPPER));
  }

  private static boolean isWithinUsableDistance(IWorldPosCallable worldPos, PlayerEntity playerIn, ITag<Block> targetTag) {
    return worldPos.applyOrElse((p_216960_2_, p_216960_3_) -> {
      return !p_216960_2_.getBlockState(p_216960_3_).isIn(targetTag) ? false : playerIn.getDistanceSq((double)p_216960_3_.getX() + 0.5D, (double)p_216960_3_.getY() + 0.5D, (double)p_216960_3_.getZ() + 0.5D) <= 64.0D;
    }, true);
  }
}
