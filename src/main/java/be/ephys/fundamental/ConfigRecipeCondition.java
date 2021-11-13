package be.ephys.fundamental;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigRecipeCondition implements ICondition {

  private final ConfigSynchronizer.BuiltConfig config;
  private final String configKey;
  private final ResourceLocation conditionId;

  public ConfigRecipeCondition(ConfigSynchronizer.BuiltConfig config, String configKey, ResourceLocation conditionId) {
    this.config = config;
    this.configKey = configKey;
    this.conditionId = conditionId;
  }

  @Override
  public ResourceLocation getID() {
    return conditionId;
  }

  @Override
  public boolean test() {
    if (configKey.contains("%")) {
      throw new RuntimeException("Illegal config key: " + configKey);
    }

    try {
      ForgeConfigSpec.ConfigValue<Boolean> configValue = (ForgeConfigSpec.ConfigValue<Boolean>) config.getConfigValue(configKey);

      return configValue.get();
    } catch (Throwable e) {
      throw new RuntimeException("[ConfigRecipeCondition] failed to read boolean configuration " + configKey, e);
    }
  }

  public static class Serializer implements IConditionSerializer<ConfigRecipeCondition> {
    private final ResourceLocation conditionId;
    private final ConfigSynchronizer.BuiltConfig commonConfig;

    public Serializer(ConfigSynchronizer.BuiltConfig commonConfig, ResourceLocation conditionId) {
      this.conditionId = conditionId;
      this.commonConfig = commonConfig;
    }

    @Override
    public void write(JsonObject json, ConfigRecipeCondition value) {
      json.addProperty("config", value.configKey);
    }

    @Override
    public ConfigRecipeCondition read(JsonObject json) {
      return new ConfigRecipeCondition(commonConfig, json.getAsJsonPrimitive("config").getAsString(), conditionId);
    }

    @Override
    public ResourceLocation getID() {
      return conditionId;
    }
  }
}
