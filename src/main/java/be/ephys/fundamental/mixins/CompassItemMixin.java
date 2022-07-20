package be.ephys.fundamental.mixins;

import be.ephys.fundamental.named_lodestone.LodestoneCompassUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompassItem.class)
public class CompassItemMixin {

  @Inject(method = "addLodestoneTags", at = @At("RETURN"))
  private void addLodestoneTags$setLodestoneName(ResourceKey<Level> dimension, BlockPos pos, CompoundTag nbt, CallbackInfo ci) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

    // client-side
    if (server == null) {
      return;
    }

    Level world = server.getLevel(dimension);

    if (world == null) {
      return;
    }

    Component name = LodestoneCompassUtils.getSignName(world, pos);
    LodestoneCompassUtils.setLodestoneName(nbt, name);
  }
}
