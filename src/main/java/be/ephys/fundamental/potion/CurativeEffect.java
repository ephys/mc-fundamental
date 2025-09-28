package be.ephys.fundamental.potion;

import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class CurativeEffect extends InstantenousMobEffect {
  protected CurativeEffect(MobEffectCategory category, int color) {
    super(category, color);
  }

  @Override
  public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
    List<MobEffect> effectsToRemove = livingEntity.getActiveEffectsMap().keySet().stream()
      .filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL && effect != this)
      .toList();

    for (MobEffect effect : effectsToRemove) {
      livingEntity.removeEffect(effect);
    }
  }
}
