package be.ephys.fundamental.mixins;

import be.ephys.fundamental.BannerLayerLimitBreaker;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BannerDuplicateRecipe.class)
public class BannerDuplicateRecipeMixin {
  @ModifyConstant(
    method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z",
    constant = @Constant(intValue = 6, ordinal = 0)
  )
  private int matches$bannerLayerLimit(int constant) {
    return BannerLayerLimitBreaker.layerLimit.get();
  }

  @ModifyConstant(
    method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;)Lnet/minecraft/world/item/ItemStack;",
    constant = @Constant(intValue = 6, ordinal = 0)
  )
  private int assemble$bannerLayerLimit(int constant) {
    return BannerLayerLimitBreaker.layerLimit.get();
  }
}
