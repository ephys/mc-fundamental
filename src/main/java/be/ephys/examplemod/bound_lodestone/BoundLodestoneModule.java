package be.ephys.examplemod.bound_lodestone;

import be.ephys.examplemod.Mod;
import be.ephys.examplemod.ModRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.function.Supplier;

public class BoundLodestoneModule {
  public static final Feature<NoFeatureConfig> BoundLodestoneFeature = new BoundLodestoneFeature(NoFeatureConfig.field_236558_a_); // .CODEC
  public static final ConfiguredFeature<?, ?> BoundLodestoneConfiguredFeature = BoundLodestoneFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);

  // TODO require high level pickaxe to break
  public static final RegistryObject<Block> BOUND_LODESTONE = ModRegistry.BLOCKS.register("bound_lodestone", () ->
    new BoundLodestoneBlock(AbstractBlock.Properties.create(Material.ANVIL).setRequiresTool().hardnessAndResistance(3.5F).sound(SoundType.LODESTONE))
  );

  public static final RegistryObject<Item> BOUND_LODESTONE_ITEM = ModRegistry.ITEMS.register("bound_lodestone", () ->
    new BlockItem(BOUND_LODESTONE.get(), new Item.Properties().group(ItemGroup.DECORATIONS))
  );

  public static TileEntityType<BoundLodestoneTileEntity> boundLodestoneTeType;

  public static void init() {
    IEventBus modEventBus =  FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addGenericListener(Feature.class, BoundLodestoneModule::registerFeatures);
    modEventBus.addGenericListener(TileEntityType.class, BoundLodestoneModule::registerTileEntities);
    MinecraftForge.EVENT_BUS.addListener(BoundLodestoneModule::addStructuresToWorldgen);
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
