package be.ephys.fundamental.mixins;

import be.ephys.fundamental.CraftingTableModule;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {

  @Shadow
  @Final
  private ContainerLevelAccess access;

  @Inject(method = "stillValid", at = @At("RETURN"), cancellable = true)
  public void stillValid$useTagSystem(Player playerIn, CallbackInfoReturnable<Boolean> cir) {
    boolean canInteractWith = cir.getReturnValue();

    if (canInteractWith) {
      return;
    }

    cir.setReturnValue(isWithinUsableDistance(this.access, playerIn, CraftingTableModule.CRAFTING_TABLE_TAG_WRAPPER));
  }

  private static boolean isWithinUsableDistance(ContainerLevelAccess worldPos, Player playerIn, TagKey<Block> targetTag) {
    return worldPos.evaluate((level, pos) -> {
      if (!level.getBlockState(pos).is(targetTag)) {
        return false;
      }

      return playerIn.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }, true);
  }
}
