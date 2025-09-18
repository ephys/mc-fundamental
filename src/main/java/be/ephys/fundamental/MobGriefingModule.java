package be.ephys.fundamental;

import be.ephys.cookiecore.config.Config;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Predicate;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(
  modid = Mod.MODID,
  bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD
)
public class MobGriefingModule {

  @Config(name = "disallowed_mob_griefers", description = "An entity target selector defining which mobs are not allowed to grief. Uses the same syntax as in commands. Example: @e[type=minecraft:creeper,type=minecraft:enderman]")
  @Config.StringDefault("")
  public static ForgeConfigSpec.ConfigValue<String> disallowedMobGriefers;

  public static final Predicate<Entity> DEFAULT_ENTITY_PREDICATE = entity -> false;
  private static Predicate<Entity> disallowedMobGriefersPredicate = DEFAULT_ENTITY_PREDICATE;

  private static Predicate<Entity> parseTargetSelector(String selectorString) {
    if (selectorString.isEmpty()) {
      return DEFAULT_ENTITY_PREDICATE;
    }

    StringReader selectorReader = new StringReader(selectorString);
    EntitySelector selector;
    try {
      selector = EntityArgument.entities().parse(selectorReader);
    } catch (CommandSyntaxException e) {
      throw new RuntimeException("disallowed_mob_griefers: Selector " + selectorString + " is not a valid entity selector.", e);
    }

    return selector.getPredicate(Vec3.ZERO);
  }

  @SubscribeEvent
  public static void onConfigChanged(ModConfigEvent.Reloading event) {
    disallowedMobGriefersPredicate = parseTargetSelector(disallowedMobGriefers.get());
  }

  @SubscribeEvent
  public static void onModSetup(FMLCommonSetupEvent event) {
    MinecraftForge.EVENT_BUS.addListener(MobGriefingModule::onMobGrief);
  }

  public static void onMobGrief(EntityMobGriefingEvent event) {
    if (disallowedMobGriefersPredicate.test(event.getEntity())) {
      event.setResult(Event.Result.DENY);
    }
  }
}
