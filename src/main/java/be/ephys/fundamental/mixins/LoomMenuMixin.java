package be.ephys.fundamental.mixins;

import be.ephys.fundamental.BannerLayerLimitBreaker;
import net.minecraft.world.inventory.LoomMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoomMenu.class)
public class LoomMenuMixin {
  @ModifyConstant(
    method = "slotsChanged",
    constant = @Constant(intValue = 6, ordinal = 0)
  )
  private int slotsChanged$bannerLayerLimit(int constant) {
    return BannerLayerLimitBreaker.layerLimit.get();
  }
}
