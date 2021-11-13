package be.ephys.fundamental;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import be.ephys.fundamental.bound_lodestone.BoundLodestoneModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@net.minecraftforge.fml.common.Mod(Mod.MODID)
public class Mod {
  public static final String MODID = "fundamental";
  public static final Logger LOGGER = LogManager.getLogger();

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

  public Mod() {
    Map<ModConfig.Type, Pair<ConfigSynchronizer.BuiltConfig, ForgeConfigSpec>> configs = ConfigSynchronizer.synchronizeConfig();
    ConfigSynchronizer.BuiltConfig commonConfig = configs.get(ModConfig.Type.COMMON).getLeft();

    Mod.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    Mod.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

    ForgeRegistries.RECIPE_SERIALIZERS.register(ExclusionRecipe.SERIALIZER);
    CraftingHelper.register(new ConfigRecipeCondition.Serializer(commonConfig, Mod.id("boolean_config")));

    // features

    CraftingTableModule.init();
    BoundLodestoneModule.init();
    // TODO: mossy config
  }

  public static ResourceLocation id(String resourceId) {
    return new ResourceLocation(MODID, resourceId);
  }
}
