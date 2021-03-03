package be.ephys.fundamental;

import be.ephys.fundamental.bound_lodestone.BoundLodestoneModule;
import be.ephys.fundamental.named_lodestone.NamedLodeStoneEventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@net.minecraftforge.fml.common.Mod(Mod.MODID)
public class Mod {
  public static final String MODID = "fundamental";
  public static final Logger LOGGER = LogManager.getLogger();

  public Mod() {
    ModRegistry.init();
    BoundLodestoneModule.init();

    // make sign "pass-through" for lodestones, unless passthroughsigns is installed as they handle it already
    MinecraftForge.EVENT_BUS.addListener(NamedLodeStoneEventHandler::onRightClickSignWithCompass);

    ForgeRegistries.RECIPE_SERIALIZERS.register(ExclusionRecipe.SERIALIZER);
  }

  public static ResourceLocation id(String resourceId) {
    return new ResourceLocation(MODID, resourceId);
  }
}
