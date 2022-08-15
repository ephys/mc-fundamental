package be.ephys.fundamental;

import be.ephys.cookiecore.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class BedTooFarFix {
  @Config(name = "bed_never_too_far", description = "Changes how a bed reachability is determined so the bed is never considered too far if the player can interact with it.")
  @Config.BooleanDefault(false)
  public static ForgeConfigSpec.BooleanValue enabled;
}
