package be.ephys.examplemod;

import be.ephys.examplemod.bound_lodestone.BoundLodestoneModule;
import be.ephys.examplemod.named_lodestone.NamedLodeStoneEventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@net.minecraftforge.fml.common.Mod(Mod.MODID)
public class Mod {
  public static final String MODID = "examplemod";
  public static final Logger LOGGER = LogManager.getLogger();

  public Mod() {
    ModRegistry.init();
    BoundLodestoneModule.init();

    MinecraftForge.EVENT_BUS.addListener(NamedLodeStoneEventHandler::onRightClickSignWithCompass);

    ForgeRegistries.RECIPE_SERIALIZERS.register(ExclusionRecipe.SERIALIZER);
  }

  public static ResourceLocation id(String resourceId) {
    return new ResourceLocation(MODID, resourceId);
  }
}
