package be.ephys.fundamental.plant_height;

import be.ephys.cookiecore.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class PlantHeightModule {
  @Config(
    name = "randomized_plant_height.cactus_enabled",
    description = "Randomizes the height of cacti."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue cactusEnabled;

  @Config(
    name = "randomized_plant_height.cactus_min",
    description = "Minimum height of a cactus (in blocks). Cacti will grow to at least this size."
  )
  @Config.IntDefault(2)
  public static ForgeConfigSpec.IntValue cactusMin;

  @Config(
    name = "randomized_plant_height.cactus_max",
    description = "Maximum height of a cactus (in blocks). Cacti will grow to at most this size."
  )
  @Config.IntDefault(4)
  public static ForgeConfigSpec.IntValue cactusMax;

  @Config(
    name = "randomized_plant_height.sugar_cane_enabled",
    description = "Randomizes the height of sugar canes."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue sugarCaneEnabled;

  @Config(
    name = "randomized_plant_height.sugar_cane_min",
    description = "Minimum height of sugar canes (in blocks). Sugar canes will grow to at least this size."
  )
  @Config.IntDefault(2)
  public static ForgeConfigSpec.IntValue sugarCaneMin;

  @Config(
    name = "randomized_plant_height.sugar_cane_max",
    description = "Maximum height of sugar canes (in blocks). Sugar canes will grow to at most this size."
  )
  @Config.IntDefault(4)
  public static ForgeConfigSpec.IntValue sugarCaneMax;
}
