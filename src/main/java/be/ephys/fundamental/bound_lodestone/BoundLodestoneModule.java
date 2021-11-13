package be.ephys.fundamental.bound_lodestone;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.Mod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.function.Supplier;

public class BoundLodestoneModule {
  @Config(name = "lodestone.bound_lodestone", description = "Adds a lodestone proxy that when used sets your compass to another lodestone.")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue enabled;

  public static final Feature<NoFeatureConfig> BoundLodestoneFeature = new BoundLodestoneFeature(NoFeatureConfig.field_236558_a_); // .CODEC
  public static final ConfiguredFeature<?, ?> BoundLodestoneConfiguredFeature = BoundLodestoneFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);

  public static final RegistryObject<Block> BOUND_LODESTONE = Mod.BLOCKS.register("bound_lodestone", () ->
    new BoundLodestoneBlock(AbstractBlock.Properties.create(Material.ANVIL).setRequiresTool().hardnessAndResistance(3.5F).sound(SoundType.LODESTONE))
  );

  public static final RegistryObject<Item> BOUND_LODESTONE_ITEM = Mod.ITEMS.register("bound_lodestone", () ->
    new BlockItem(BOUND_LODESTONE.get(), new Item.Properties().group(ItemGroup.DECORATIONS))
  );

  public static TileEntityType<BoundLodestoneTileEntity> boundLodestoneTeType;

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addGenericListener(TileEntityType.class, BoundLodestoneModule::registerTileEntities);

    if (enabled.get()) {
      MinecraftForge.EVENT_BUS.addListener(BoundLodestoneModule::onVillagerTrades);
    }

    // Upcoming
    // modEventBus.addGenericListener(Feature.class, BoundLodestoneModule::registerFeatures);
    // MinecraftForge.EVENT_BUS.addListener(BoundLodestoneModule::addStructuresToWorldgen);
  }

  public static void onVillagerTrades(VillagerTradesEvent event) {
    if (event.getType() == VillagerProfession.CARTOGRAPHER) {
      event.getTrades().get(3).add(
        new BasicTrade(
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

  public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt) {
    TileEntityType<BoundLodestoneTileEntity> type = TileEntityType.Builder.create(BoundLodestoneTileEntity::new, BOUND_LODESTONE.get()).build(null);
    type.setRegistryName(Mod.id("bound_lodestone"));
    evt.getRegistry().register(type);

    boundLodestoneTeType = type;
  }

  public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
    registerFeature(event.getRegistry(), BoundLodestoneFeature, Mod.id("bound_lodestone"));

    Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;
    Registry.register(registry, Mod.id("bound_lodestone"), BoundLodestoneConfiguredFeature);
  }

  public static void addStructuresToWorldgen(BiomeLoadingEvent event) {
    List<Supplier<ConfiguredFeature<?, ?>>> features = event.getGeneration().getFeatures(GenerationStage.Decoration.SURFACE_STRUCTURES);

    features.add(() -> BoundLodestoneConfiguredFeature);
  }

  // source: https://github.com/TelepathicGrunt/RepurposedStructures/blob/master/src/main/java/com/telepathicgrunt/repurposedstructures/RSFeatures.java
  public static <F extends Feature<?>> void registerFeature(
    IForgeRegistry<Feature<?>> registry,
    F feature,
    ResourceLocation resourceLocation
  ) {
    feature.setRegistryName(resourceLocation);

    // Have to do this as Minecraft will otherwise think the feature isn't registered.
    // Hopefully this means people can make custom ConfiguredFeatures by datapack with the feature.
    Registry.register(Registry.FEATURE, resourceLocation, feature);
  }
}
