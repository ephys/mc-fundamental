package be.ephys.fundamental;

import be.ephys.cookiecore.config.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(
  modid = Mod.MODID,
  bus = EventBusSubscriber.Bus.MOD
)
public class ElderGuardianModule {
  @Config(name = "renewable_elder_guardians", description = "Guardians turn into Elder Guardians when struck by lightning.")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue enabled;

  @SubscribeEvent
  public static void onCommonSetup(FMLCommonSetupEvent event) {
    if (enabled.get()) {
      MinecraftForge.EVENT_BUS.addListener(ElderGuardianModule::onLightningStrike);
    }
  }

  public static void onLightningStrike(EntityStruckByLightningEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof Mob mob)) {
      return;
    }

    if (mob.getType() != EntityType.GUARDIAN) {
      return;
    }

    if (!ForgeEventFactory.canLivingConvert(mob, EntityType.ZOMBIFIED_PIGLIN, (timer) -> {})) {
      return;
    }

    ElderGuardian elderGuardian = EntityType.ELDER_GUARDIAN.create(entity.getLevel());
    elderGuardian.moveTo(entity.getX(), entity.getY(), entity.getZ());
    elderGuardian.setNoAi(mob.isNoAi());

    // I'd be curious to find out what a baby elder guardian is
    elderGuardian.setBaby(mob.isBaby());

    if (mob.hasCustomName()) {
      elderGuardian.setCustomName(mob.getCustomName());
      elderGuardian.setCustomNameVisible(mob.isCustomNameVisible());
    }

    mob.getLevel().addFreshEntity(elderGuardian);
    elderGuardian.persistenceRequired = mob.isPersistenceRequired();

    ForgeEventFactory.onLivingConvert(mob, elderGuardian);

    mob.discard();

    event.setCanceled(true);
  }
}
