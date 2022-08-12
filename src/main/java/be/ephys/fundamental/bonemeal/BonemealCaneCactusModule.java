package be.ephys.fundamental.bonemeal;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.plant_height.PlantHeightModule;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Random;

@Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = Mod.EventBusSubscriber.Bus.MOD
)
public class BonemealCaneCactusModule {
  private static final Random random = new Random();

  @Config(
    name = "bonemeal.cactus",
    description = "Make cactus grow if bone meal is used on it."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue cactusEnabled;

  @Config(
    name = "bonemeal.sugarCane",
    description = "Make sugar canes grow if bone meal is used on it."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue caneEnabled;

  @SubscribeEvent
  public static void onCommonSetup(FMLCommonSetupEvent t) {
    if (cactusEnabled.get() || caneEnabled.get()) {
      MinecraftForge.EVENT_BUS.addListener(BonemealCaneCactusModule::onBoneMealUse);
    }
  }

  public static void onBoneMealUse(BonemealEvent event) {
    BlockState targetBlock = event.getBlock();

    if ((!cactusEnabled.get() || !targetBlock.is(Blocks.CACTUS)) && (!caneEnabled.get() || !targetBlock.is(Blocks.SUGAR_CANE))) {
      return;
    }

    System.out.println("grow!");
    var result = PlantHeightModule.growCactusOrSugarCane(targetBlock, event.getWorld(), event.getPos(), random, true);

    if (result) {
      event.setResult(Event.Result.ALLOW);
    } else {
      event.setResult(Event.Result.DENY);
    }
  }
}
