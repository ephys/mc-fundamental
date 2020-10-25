package be.ephys.examplemod;

import be.ephys.examplemod.named_lodestone.NamedLodeStoneEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
  public static final String MODID = "examplemod";
  private static final Logger LOGGER = LogManager.getLogger();

  public ExampleMod() {
    ModRegistry.init();

    MinecraftForge.EVENT_BUS.addListener(NamedLodeStoneEventHandler::onRightClickSignWithCompass);
  }
}
