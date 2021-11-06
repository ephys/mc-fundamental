package be.ephys.fundamental.bonemeal_grass;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.Mod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BonemealGrassModule {
  @Config(
    name = "spread_grass_with_bonemeal.mapping",
    description = "Adds the Nylium spread mechanic to other grass types. This is a mapping of which grass will spread on which blocks."
      + "\nOne mapping per array entry."
      + "\nEach entry has 3 parts separated by a space:"
      + "\n<dirt_block>(block ID) <source_grass_block>(block ID or block #tag) <generated_grass_block>(block ID)"
  )
  @Config.StringListDefault({
    // vanilla

    "minecraft:dirt #fundamental:grass_blocks minecraft:grass_block",
    "minecraft:dirt #fundamental:myceliums minecraft:mycelium",

    // byg

    "minecraft:blackstone #fundamental:crimson_nylium byg:overgrown_crimson_blackstone",

    "minecraft:stone #fundamental:grass_blocks byg:overgrown_stone",
    "byg:dacite #fundamental:grass_blocks byg:overgrown_dacite",
    "byg:mewdow_dirt #fundamental:grass_blocks byg:meadow_grass_block",
    "minecraft:netherrack #fundamental:grass_blocks byg:overgrown_netherrack",
    "minecraft:netherrack #fundamental:myceliums byg:mycelium_netherrack",
    "minecraft:netherrack byg:sythian_nylium byg:sythian_nylium",

    "byg:blue_netherrack byg:embur_nylium byg:embur_nylium",
    "byg:ether_soil byg:ether_phylium byg:ether_phylium",
    "minecraft:soul_soil byg:wailing_nylium byg:wailing_nylium",
    "minecraft:end_stone byg:ivis_phylium byg:ivis_phylium",
    "minecraft:end_stone byg:shulkren_phylium byg:shulkren_phylium",
    "minecraft:end_stone byg:nightshade_phylium byg:nightshade_phylium",
    "minecraft:end_stone byg:bulbis_phycelium byg:bulbis_phycelium",

    // botania/quark

    "minecraft:dirt botania:dry_grass botania:dry_grass",
    "minecraft:dirt botania:golden_grass botania:golden_grass",
    "minecraft:dirt botania:vivid_grass botania:vivid_grass",
    "minecraft:dirt botania:scorched_grass botania:scorched_grass",
    "minecraft:dirt botania:infused_grass botania:infused_grass",
    "minecraft:dirt botania:mutated_grass botania:mutated_grass",
    "minecraft:dirt quark:glowcelium quark:glowcelium",

    // tinker's

    "minecraft:dirt #fundamental:tconstruct/sky_x_slime_grasses tconstruct:sky_vanilla_slime_grass",
    "minecraft:dirt #fundamental:tconstruct/ender_x_slime_grasses tconstruct:ender_vanilla_slime_grass",
    "minecraft:dirt #fundamental:tconstruct/blood_x_slime_grasses tconstruct:blood_vanilla_slime_grass",
    "minecraft:dirt #fundamental:tconstruct/earth_x_slime_grasses tconstruct:earth_vanilla_slime_grass",

    "tconstruct:earth_slime_dirt #fundamental:tconstruct/sky_x_slime_grasses tconstruct:sky_earth_slime_grass",
    "tconstruct:earth_slime_dirt #fundamental:tconstruct/ender_x_slime_grasses tconstruct:ender_earth_slime_grass",
    "tconstruct:earth_slime_dirt #fundamental:tconstruct/blood_x_slime_grasses tconstruct:blood_earth_slime_grass",
    "tconstruct:earth_slime_dirt #fundamental:tconstruct/earth_x_slime_grasses tconstruct:earth_earth_slime_grass",

    "tconstruct:sky_slime_dirt #fundamental:tconstruct/sky_x_slime_grasses tconstruct:sky_sky_slime_grass",
    "tconstruct:sky_slime_dirt #fundamental:tconstruct/ender_x_slime_grasses tconstruct:ender_sky_slime_grass",
    "tconstruct:sky_slime_dirt #fundamental:tconstruct/blood_x_slime_grasses tconstruct:blood_sky_slime_grass",
    "tconstruct:sky_slime_dirt #fundamental:tconstruct/earth_x_slime_grasses tconstruct:earth_sky_slime_grass",

    "tconstruct:ender_slime_dirt #fundamental:tconstruct/sky_x_slime_grasses tconstruct:sky_ender_slime_grass",
    "tconstruct:ender_slime_dirt #fundamental:tconstruct/ender_x_slime_grasses tconstruct:ender_ender_slime_grass",
    "tconstruct:ender_slime_dirt #fundamental:tconstruct/blood_x_slime_grasses tconstruct:blood_ender_slime_grass",
    "tconstruct:ender_slime_dirt #fundamental:tconstruct/earth_x_slime_grasses tconstruct:earth_ender_slime_grass",

    "tconstruct:ichor_slime_dirt #fundamental:tconstruct/sky_x_slime_grasses tconstruct:sky_ichor_slime_grass",
    "tconstruct:ichor_slime_dirt #fundamental:tconstruct/ender_x_slime_grasses tconstruct:ender_ichor_slime_grass",
    "tconstruct:ichor_slime_dirt #fundamental:tconstruct/blood_x_slime_grasses tconstruct:blood_ichor_slime_grass",
    "tconstruct:ichor_slime_dirt #fundamental:tconstruct/earth_x_slime_grasses tconstruct:earth_ichor_slime_grass",
  })
  public static ForgeConfigSpec.ConfigValue<List<String>> spreadablesMappingRaw;

  public static boolean isEnabled() {
    return spreadablesMappingRaw.get().size() > 0;
  }

  public static void onCommonSetup(FMLCommonSetupEvent t) {
    // initialize mapping
    getSpreadableMapping();
  }

  private static class SpreadableMappingEntry {
    public final Block dirtBlock;
    public final Block sourceBlock;
    public final ITag<Block> sourceTag;
    public final Block generatedBlock;

    private SpreadableMappingEntry(Block dirtBlock, Block sourceBlock, ITag<Block> sourceTag, Block generatedBlock) {
      this.dirtBlock = dirtBlock;
      this.sourceBlock = sourceBlock;
      this.sourceTag = sourceTag;
      this.generatedBlock = generatedBlock;
    }

    boolean sourceIsTag() {
      return this.sourceBlock == null;
    }
  }

  private static Map<Block, List<SpreadableMappingEntry>> spreadableMappingCache;

  private static Map<String, ITag<Block>> tagMap = new HashMap<>();

  private static Map<Block, List<SpreadableMappingEntry>> getSpreadableMapping() {
    if (spreadableMappingCache == null) {
      spreadableMappingCache = new HashMap<>();

      for (String entry : spreadablesMappingRaw.get()) {
        String[] parts = entry.split(" ");
        if (parts.length != 3) {
          Mod.LOGGER.error("[spread_grass_with_bonemeal.mapping] invalid entry " + entry);
          continue;
        }

        Block dirt = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0]));
        if (dirt == Blocks.AIR) {
          Mod.LOGGER.warn("[spread_grass_with_bonemeal.mapping] ignoring entry " + entry + ". Block " + parts[0] + " was not found");
          continue;
        }

        // ==

        String spreadableId = parts[1];
        ITag<Block> spreadableTag = null;
        Block spreadableBlock = null;
        if (spreadableId.startsWith("#")) {
          String spreadableTagId = spreadableId.substring(1);

          // if we create a tag every time,
          spreadableTag = tagMap.containsKey(spreadableTagId)
            ? tagMap.get(spreadableTagId)
            : BlockTags.createOptional(new ResourceLocation(spreadableTagId));

          tagMap.put(spreadableTagId, spreadableTag);
        } else {
          spreadableBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(spreadableId));
          if (spreadableBlock == Blocks.AIR) {
            spreadableBlock = null;
          }
        }

        if (spreadableBlock == null && spreadableTag == null) {
          Mod.LOGGER.warn("[spread_grass_with_bonemeal.mapping] ignoring entry " + entry + ". Block or Tag " + spreadableId + " were not found");
          continue;
        }

        // ==

        Block grass = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[2]));
        if (grass == Blocks.AIR) {
          Mod.LOGGER.warn("[spread_grass_with_bonemeal.mapping] ignoring entry " + entry + ". Block " + parts[2] + " was not found");
          continue;
        }

        if (!spreadableMappingCache.containsKey(dirt)) {
          spreadableMappingCache.put(dirt, new ArrayList<>());
        }

        spreadableMappingCache.get(dirt).add(new SpreadableMappingEntry(dirt, spreadableBlock, spreadableTag, grass));
      }
    }

    return spreadableMappingCache;
  }

  @SubscribeEvent
  public void onBoneMealUse(BonemealEvent event) {
    BlockState dirtBlock = event.getBlock();

    Map<Block, List<SpreadableMappingEntry>> dirtToGrassMapping = getSpreadableMapping();
    List<SpreadableMappingEntry> spreadCandidates = dirtToGrassMapping.get(dirtBlock.getBlock());

    if (spreadCandidates == null || spreadCandidates.isEmpty()) {
      return;
    }

    World world = event.getWorld();
    BlockPos dirtBlockPos = event.getPos();
    Block resultingBlock = null;

    stop:
    for (BlockPos blockpos : BlockPos.getAllInBoxMutable(dirtBlockPos.add(-1, -1, -1), dirtBlockPos.add(1, 1, 1))) {
      BlockState neighborBlock = world.getBlockState(blockpos);

      // TODO: should we do a random selection instead of first come?
      for (SpreadableMappingEntry spreadCandidate : spreadCandidates) {
        boolean matches = spreadCandidate.sourceIsTag()
          ? neighborBlock.isIn(spreadCandidate.sourceTag)
          : neighborBlock.isIn(spreadCandidate.sourceBlock);

        if (matches) {
          resultingBlock = spreadCandidate.generatedBlock;

          break stop;
        }
      }
    }

    if (resultingBlock != null) {
      world.setBlockState(dirtBlockPos, resultingBlock.getDefaultState(), 3);
      event.setResult(Event.Result.ALLOW);
    }
  }
}
