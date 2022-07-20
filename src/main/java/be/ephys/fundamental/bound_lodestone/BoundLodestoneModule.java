package be.ephys.fundamental.bound_lodestone;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.Mod;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD
)
public class BoundLodestoneModule {
  @Config(name = "lodestone.bound_lodestone", description = "Adds a lodestone proxy that when used sets your compass to another lodestone.")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue enabled;

//  public static final Feature<NoFeatureConfig> BoundLodestoneFeature = new BoundLodestoneFeature(NoFeatureConfig.field_236558_a_); // .CODEC
//  public static final ConfiguredFeature<?, ?> BoundLodestoneConfiguredFeature = BoundLodestoneFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);

  public static final RegistryObject<Block> BOUND_LODESTONE = Mod.BLOCKS.register("bound_lodestone", () ->
    new BoundLodestoneBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LODESTONE))
  );

  public static final RegistryObject<Item> BOUND_LODESTONE_ITEM = Mod.ITEMS.register("bound_lodestone", () ->
    new BlockItem(BOUND_LODESTONE.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS))
  );

  public static final RegistryObject<BlockEntityType<BoundLodestoneBlockEntity>> BOUND_LODESTONE_TE_TYPE = Mod.BLOCK_ENTITIES.register("bound_lodestone", () ->
    BlockEntityType.Builder.of(BoundLodestoneBlockEntity::new, BOUND_LODESTONE.get()).build(null)
  );

  @SubscribeEvent
  public static void onCommonSetup(FMLCommonSetupEvent event) {
    if (enabled.get()) {
      MinecraftForge.EVENT_BUS.addListener(BoundLodestoneModule::onVillagerTrades);
    }

    // Upcoming
    // modEventBus.addGenericListener(Feature.class, BoundLodestoneModule::registerFeatures);
    // MinecraftForge.EVENT_BUS.addListener(BoundLodestoneModule::addStructuresToLevelgen);
  }

  public static void onVillagerTrades(VillagerTradesEvent event) {
    if (event.getType() == VillagerProfession.CARTOGRAPHER) {
      event.getTrades().get(3).add(
        new BasicItemListing(
          /* price */
          new ItemStack(Items.LODESTONE),
          /* price */
          new ItemStack(Items.EMERALD, 11),
          /* buy */
          new ItemStack(BOUND_LODESTONE_ITEM.get()),
          /* max count */
          8,
          /* xp */
          15,
          /* price mult */
          1f
        )
      );
    }
  }

//  public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
//    registerFeature(event.getRegistry(), BoundLodestoneFeature, Mod.id("bound_lodestone"));
//
//    Registry<ConfiguredFeature<?, ?>> registry = LevelGenRegistries.CONFIGURED_FEATURE;
//    Registry.register(registry, Mod.id("bound_lodestone"), BoundLodestoneConfiguredFeature);
//  }

//  public static void addStructuresToLevelgen(BiomeLoadingEvent event) {
//    List<Supplier<ConfiguredFeature<?, ?>>> features = event.getGeneration().getFeatures(GenerationStage.Decoration.SURFACE_STRUCTURES);
//
//    features.add(() -> BoundLodestoneConfiguredFeature);
//  }

  // source: https://github.com/TelepathicGrunt/RepurposedStructures/blob/master/src/main/java/com/telepathicgrunt/repurposedstructures/RSFeatures.java
//  public static <F extends Feature<?>> void registerFeature(
//    IForgeRegistry<Feature<?>> registry,
//    F feature,
//    ResourceLocation resourceLocation
//  ) {
//    feature.setRegistryName(resourceLocation);
//
//    // Have to do this as Minecraft will otherwise think the feature isn't registered.
//    // Hopefully this means people can make custom ConfiguredFeatures by datapack with the feature.
//    Registry.register(Registry.FEATURE, resourceLocation, feature);
//  }
}
