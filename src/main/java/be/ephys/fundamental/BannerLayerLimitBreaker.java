package be.ephys.fundamental;

import be.ephys.cookiecore.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class BannerLayerLimitBreaker {
  @Config(name = "banner_layer_limit", description = """
How many layers can be added to a banner. Survival vanilla is 6, command block vanilla is 16.

Compatibility Note: Quark implements this too, but forgot to add support to the Banner Duplication recipe.
To remain compatible with Quark, we disable this feature if Quark is detected except for the Banner Duplication Recipe.
For this reason, we recommend using the same value for both mods.
  """)
  @Config.IntDefault(value = 16, min = 1, max = 16)
  public static ForgeConfigSpec.IntValue layerLimit;
}
