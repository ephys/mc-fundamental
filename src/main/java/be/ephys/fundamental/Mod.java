package be.ephys.fundamental;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import be.ephys.fundamental.bonemeal_grass.BonemealGrassModule;
import be.ephys.fundamental.bound_lodestone.BoundLodestoneModule;
import be.ephys.fundamental.named_lodestone.NamedLodeStoneFeature;
import be.ephys.fundamental.slime_on_piston.SlimeOnPistonFeature;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@net.minecraftforge.fml.common.Mod(Mod.MODID)
public class Mod {
  public static final String MODID = "fundamental";
  public static final Logger LOGGER = LogManager.getLogger();

  public Mod() {
    ConfigSynchronizer.synchronizeConfig();

    CraftingTableModule.init();
    BoundLodestoneModule.init();

    // make sign "pass-through" for lodestones, unless passthroughsigns is installed as they handle it already
    MinecraftForge.EVENT_BUS.register(new NamedLodeStoneFeature());
    MinecraftForge.EVENT_BUS.register(new SlimeOnPistonFeature());
    MinecraftForge.EVENT_BUS.register(new BonemealGrassModule());

    FMLJavaModLoadingContext.get().getModEventBus().addListener(BonemealGrassModule::onCommonSetup);

    ForgeRegistries.RECIPE_SERIALIZERS.register(ExclusionRecipe.SERIALIZER);
  }

  public static ResourceLocation id(String resourceId) {
    return new ResourceLocation(MODID, resourceId);
  }
}
