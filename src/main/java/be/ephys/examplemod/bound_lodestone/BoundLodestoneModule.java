package be.ephys.examplemod.bound_lodestone;

import be.ephys.examplemod.Mod;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.function.Supplier;

public class BoundLodestoneModule {
  public static final Feature<NoFeatureConfig> BoundLodestoneFeature = new BoundLodestoneFeature(NoFeatureConfig.field_236558_a_); // .CODEC
  public static final ConfiguredFeature<?, ?> BoundLodestoneConfiguredFeature = BoundLodestoneFeature.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);

  public static void init() {
    FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, BoundLodestoneModule::registerFeatures);
    MinecraftForge.EVENT_BUS.addListener(BoundLodestoneModule::addStructuresToWorldgen);
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
