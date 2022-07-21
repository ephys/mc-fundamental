package be.ephys.fundamental.plant_height;

import be.ephys.cookiecore.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class PlantHeightModule {
  @Config(
    name = "random_cactus_height.enabled",
    description = "Randomizes the height of cacti."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue cactusEnabled;

  @Config(
    name = "random_cactus_height.min",
    description = "Minimum height of a cactus (in blocks). Cacti will grow to at least this size."
  )
  @Config.IntDefault(2)
  public static ForgeConfigSpec.IntValue cactusMinHeight;

  @Config(
    name = "random_cactus_height.max",
    description = "Maximum height of a cactus (in blocks). Cacti will grow to at most this size."
  )
  @Config.IntDefault(4)
  public static ForgeConfigSpec.IntValue cactusMaxHeight;

  @Config(
    name = "random_sugar_cane_height.enabled",
    description = "Randomizes the height of sugar canes."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue sugarCaneEnabled;

  @Config(
    name = "random_sugar_cane_height.min",
    description = "Minimum height of sugar canes (in blocks). Sugar canes will grow to at least this size."
  )
  @Config.IntDefault(2)
  public static ForgeConfigSpec.IntValue sugarCaneMinHeight;

  @Config(
    name = "random_sugar_cane_height.max",
    description = "Maximum height of sugar canes (in blocks). Sugar canes will grow to at most this size."
  )
  @Config.IntDefault(4)
  public static ForgeConfigSpec.IntValue sugarCaneMaxHeight;

  @Config(
    name = "random_vine_height.enabled",
    description = "Randomizes the height of sugar canes."
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue vineEnabled;

  @Config(
    name = "random_vine_height.min",
    description = "Minimum height of vines (in blocks). Vines will grow to at least this size."
  )
  @Config.IntDefault(4)
  public static ForgeConfigSpec.IntValue vineMinHeight;

  @Config(
    name = "random_vine_height.max",
    description = "Maximum height of vines (in blocks). Vines will grow to at most this size."
  )
  @Config.IntDefault(7)
  public static ForgeConfigSpec.IntValue vineMaxHeight;

  @Config(
    name = "random_vine_height.min_floor_distance",
    description = "At which distance from the floor will the vine stop growing (at a minimum)."
  )
  @Config.IntDefault(0)
  public static ForgeConfigSpec.IntValue vineMinFloorDistance;

  @Config(
    name = "random_vine_height.max_floor_Distance",
    description = "At which distance from the floor will the vine stop growing (at a maximum)."
  )
  @Config.IntDefault(2)
  public static ForgeConfigSpec.IntValue vineMaxFloorDistance;
}
