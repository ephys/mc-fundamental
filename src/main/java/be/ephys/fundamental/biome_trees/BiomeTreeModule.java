package be.ephys.fundamental.biome_trees;

import be.ephys.cookiecore.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeTreeModule {
  @Config(name = "saplings_grow_biome_trees", description = "Makes saplings grow into the same trees that generate in the biome during world generation.")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue enabled;

  private static final RandomSource RANDOM = RandomSource.create();

  public static boolean spawnBiomeTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState saplingState, RandomSource random, ConfiguredFeature<?, ?> expectedTreeTypeCf) {
    if (!enabled.get()) {
      return false;
    }

    Biome biome = world.getBiome(pos).value();
    BiomeTreeFeatures biomeTreeFeatures = collectTreeFeaturesForBiomeCached(biome);

    List<WeightedConfiguredFeature> weightedTreeFeatures = biomeTreeFeatures.weightedTreeFeatures;
    List<ConfiguredFeature<?, ?>> defaultTreeFeatures = biomeTreeFeatures.defaultTreeFeatures;

    if (weightedTreeFeatures.isEmpty() && defaultTreeFeatures.isEmpty()) {
      return false;
    }

    TreeConfiguration expectedTreeConfiguration = getTreeConfiguration(expectedTreeTypeCf);

    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
    for (WeightedConfiguredFeature weightedFeature : weightedTreeFeatures) {
      ConfiguredFeature<?, ?> configuredFeature = weightedFeature.configuredFeature;

      if (!matchesExpectedTree(expectedTreeConfiguration, configuredFeature, pos)) {
        continue;
      }

      if (random.nextFloat() >= weightedFeature.chance) {
        continue;
      }

      if (configuredFeature.place(world, chunkGenerator, random, pos)) {
        return true;
      }
    }

    int compatibleTreeCount = 0;
    for (ConfiguredFeature<?, ?> defaultTreeFeature : defaultTreeFeatures) {
      if (matchesExpectedTree(expectedTreeConfiguration, defaultTreeFeature, pos)) {
        compatibleTreeCount++;
      }
    }

    if (compatibleTreeCount > 0) {
      int randomTreeIndex = random.nextInt(compatibleTreeCount);

      for (ConfiguredFeature<?, ?> defaultTreeFeature : defaultTreeFeatures) {
        if (!matchesExpectedTree(expectedTreeConfiguration, defaultTreeFeature, pos)) {
          continue;
        }

        if (randomTreeIndex-- > 1) {
          continue;
        }

        if (defaultTreeFeature.place(world, chunkGenerator, random, pos)) {
          return true;
        }
      }
    }

    world.setBlock(pos, saplingState, 4);

    return false;
  }

  private static boolean matchesExpectedTree(TreeConfiguration expectedTreeConfiguration, ConfiguredFeature<?, ?> treeFeature, BlockPos pos) {
    TreeConfiguration treeConfig = getTreeConfiguration(treeFeature);
    if (treeConfig == null) {
      return false;
    }

    Block treeFoliageBlock = treeConfig.foliageProvider.getState(RANDOM, pos).getBlock();
    Block expectedTreeFoliageBlock = expectedTreeConfiguration.foliageProvider.getState(RANDOM, pos).getBlock();
    if (treeFoliageBlock != expectedTreeFoliageBlock) {
      return false;
    }

    Block treeTrunkBlock = getTreeTrunkBlock(treeConfig, pos);
    Block expectedTreeTrunkBlock = expectedTreeConfiguration.trunkProvider.getState(RANDOM, pos).getBlock();
    return treeTrunkBlock == expectedTreeTrunkBlock;
  }

  private static Block getTreeTrunkBlock(TreeConfiguration treeConfig, BlockPos pos) {
    return treeConfig.trunkProvider.getState(RANDOM, pos).getBlock();
  }

  private static void collectTreeFeatures(PlacedFeature placedFeature, List<WeightedConfiguredFeature> weightedFeatures, List<ConfiguredFeature<?, ?>> defaultFeatures, float chance) {
    ConfiguredFeature<?, ?> configuredFeature = placedFeature.feature().value();

    if (configuredFeature.config() instanceof RandomFeatureConfiguration config) {
      for (WeightedPlacedFeature weightedPlacedFeature : config.features) {
        collectTreeFeatures(weightedPlacedFeature.feature.value(), weightedFeatures, defaultFeatures, chance * weightedPlacedFeature.chance);
      }

      collectTreeFeatures(config.defaultFeature.value(), weightedFeatures, defaultFeatures);

      return;
    }

    if (configuredFeature.feature() instanceof TreeFeature) {
      if (chance >= 1) {
        defaultFeatures.add(configuredFeature);
      } else {
        weightedFeatures.add(new WeightedConfiguredFeature(configuredFeature, chance));
      }
    }
  }

  private static final Map<Biome, BiomeTreeFeatures> biomeTreeFeaturesCache = new HashMap<>();

  private static BiomeTreeFeatures collectTreeFeaturesForBiomeCached(Biome biome) {
    return biomeTreeFeaturesCache.computeIfAbsent(biome, BiomeTreeModule::collectTreeFeaturesForBiome);
  }

  private static BiomeTreeFeatures collectTreeFeaturesForBiome(Biome biome) {
    BiomeGenerationSettings biomeGenSettings = biome.getGenerationSettings();
    HolderSet<PlacedFeature> vegetalFeatures = biomeGenSettings.features().get(GenerationStep.Decoration.VEGETAL_DECORATION.ordinal());

    List<WeightedConfiguredFeature> weightedTreeFeatures = new ArrayList<>();
    List<ConfiguredFeature<?, ?>> defaultTreeFeatures = new ArrayList<>();
    for (Holder<PlacedFeature> featureHolder : vegetalFeatures) {
      PlacedFeature feature = featureHolder.value();
      collectTreeFeatures(feature, weightedTreeFeatures, defaultTreeFeatures);
    }

    return new BiomeTreeFeatures(weightedTreeFeatures, defaultTreeFeatures);
  }

  private static void collectTreeFeatures(PlacedFeature placedFeature, List<WeightedConfiguredFeature> weightedFeatures, List<ConfiguredFeature<?, ?>> defaultFeatures) {
    collectTreeFeatures(placedFeature, weightedFeatures, defaultFeatures, 1);
  }

  private static TreeConfiguration getTreeConfiguration(ConfiguredFeature<?, ?> configuredFeature) {
    if (configuredFeature.config() instanceof TreeConfiguration) {
      return (TreeConfiguration) configuredFeature.config();
    }

    return null;
  }

  private record BiomeTreeFeatures(List<WeightedConfiguredFeature> weightedTreeFeatures,
                                   List<ConfiguredFeature<?, ?>> defaultTreeFeatures) {
  }

  private record WeightedConfiguredFeature(ConfiguredFeature<?, ?> configuredFeature, float chance) {
  }
}
