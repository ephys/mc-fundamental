package be.ephys.fundamental.mixins;

import be.ephys.fundamental.Mod;
import be.ephys.fundamental.potion.IPersistentEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IPersistentEffect {

  @Unique
  private boolean fundamental$persists = false;

  @Unique
  public boolean mc_fundamental$persistsAfterDeath() {
    return fundamental$persists;
  }

  @Unique
  public void mc_fundamental$setPersistsAfterDeath(boolean persist) {
    this.fundamental$persists = persist;
  }

  @Inject(method = "writeDetailsTo", at = @At("TAIL"))
  private void writeDetailsTo$persists(CompoundTag nbt, CallbackInfo ci) {
    nbt.putBoolean(Mod.MODID + ":persists", this.fundamental$persists);
  }

  @Inject(method = "load", at = @At("RETURN"))
  private static void load$persists(CompoundTag nbt, CallbackInfoReturnable<MobEffectInstance> cir) {
    MobEffectInstance mobEffectInstance = cir.getReturnValue();
    if (mobEffectInstance == null) {
      return;
    }

    ((IPersistentEffect) mobEffectInstance).mc_fundamental$setPersistsAfterDeath(nbt.getBoolean(Mod.MODID + ":persists"));
  }

  @Inject(method = "setDetailsFrom", at = @At("HEAD"))
  private void writeDetailsTo$persists(MobEffectInstance effectInstance, CallbackInfo ci) {
    this.fundamental$persists = ((IPersistentEffect) effectInstance).mc_fundamental$persistsAfterDeath();
  }
}
