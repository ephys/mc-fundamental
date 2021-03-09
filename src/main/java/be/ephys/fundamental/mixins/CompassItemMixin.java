package be.ephys.fundamental.mixins;

import be.ephys.fundamental.named_lodestone.LodestoneCompassUtils;
import net.minecraft.item.CompassItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompassItem.class)
public class CompassItemMixin {

  @Inject(method = "func_234669_a_", at = @At("RETURN"))
  private void func_234669_a_$setLodestoneName(RegistryKey<World> dimension, BlockPos pos, CompoundNBT nbt, CallbackInfo ci) {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

    // client-side
    if (server == null) {
      return;
    }

    World world = server.getWorld(dimension);

    if (world == null) {
      return;
    }

    ITextComponent name = LodestoneCompassUtils.getSignName(world, pos);
    LodestoneCompassUtils.setLodestoneName(nbt, name);
  }
}