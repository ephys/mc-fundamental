package be.ephys.fundamental;

import be.ephys.cookiecore.config.ConfigSynchronizer;
import be.ephys.fundamental.moss.MossModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
  public static final DeferredRegister<Motive> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_TYPES, MODID);

  public static final RegistryObject<Motive> FOX_PAINTING = PAINTINGS.register("fox", () -> new Motive(16, 32));

  public Mod() {
    Map<ModConfig.Type, Pair<ConfigSynchronizer.BuiltConfig, ForgeConfigSpec>> configs = ConfigSynchronizer.synchronizeConfig();
    ConfigSynchronizer.BuiltConfig commonConfig = configs.get(ModConfig.Type.COMMON).getLeft();

    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    Mod.BLOCKS.register(modEventBus);
    Mod.ITEMS.register(modEventBus);
    Mod.BLOCK_ENTITIES.register(modEventBus);
    Mod.PAINTINGS.register(modEventBus);

    ForgeRegistries.RECIPE_SERIALIZERS.register(ExclusionRecipe.SERIALIZER);
    CraftingHelper.register(new ConfigRecipeCondition.Serializer(commonConfig, Mod.id("boolean_config")));

    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MossModule::init);
  }

  public static ResourceLocation id(String resourceId) {
    return new ResourceLocation(MODID, resourceId);
  }
}
