package be.ephys.fundamental.mixins;

import be.ephys.fundamental.BannerLayerLimitBreaker;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoomScreen.class)
public class LoomScreenMixin {
  @ModifyConstant(
    method = "containerChanged",
    constant = @Constant(intValue = 6, ordinal = 0, log = true)
  )
  private int containerChanged$bannerLayerLimit(int constant) {
    return BannerLayerLimitBreaker.layerLimit.get();
  }
}
