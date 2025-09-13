package be.ephys.fundamental.moss;

import be.ephys.cookiecore.config.Config;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = Mod.EventBusSubscriber.Bus.MOD,
  value = Dist.CLIENT
)
public class MossModule {

  private static final FeatureFlagSet EMPTY_FEATURE_FLAG_SET = FeatureFlagSet.of();

  @Config(
    name = "inventory_items_respect_biome_colors",
    description = "Make items such as grass, leaves, etc... follow biome colors even when they are in the player's inventory.",
    side = ModConfig.Type.CLIENT
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue itemColorEnabled;

  @Config(
    name = "mossy_stone_bricks_follows_biome_colors",
    description = "Makes the moss on mossy stone bricks change color based on the biome."
      + "\nThis feature adds a resource pack to modify the vanilla Mossy Stone Bricks models."
      + "\nDue to a limitation in Forge, the game will reload resource packs the first time this mod is added (and if this config changes), leading to a longer initial loading time. Sorry.",
    side = ModConfig.Type.CLIENT
  )
  @Config.BooleanDefault(false)
  public static ForgeConfigSpec.BooleanValue mossyStoneBrickEnabled;

  @Config(
    name = "mossy_cobblestone_follows_biome_colors",
    description = "Makes the moss on mossy cobblestone change color based on the biome."
      + "\nThis feature adds a resource pack to modify the Mossy Cobblestone models."
      + "\nDue to a limitation in Forge, the game will reload resource packs the first time this mod is added (and if this config changes), leading to a longer initial loading time. Sorry.",
    side = ModConfig.Type.CLIENT
  )
  @Config.BooleanDefault(false)
  public static ForgeConfigSpec.BooleanValue mossyCobblestoneEnabled;

  public static final String MOSSY_COBBLESTONE_PACK_ID = "fundamental:mossy_cobblestone";
  public static final String MOSSY_STONE_BRICK_PACK_ID = "fundamental:mossy_stone_bricks";

  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    Block[] mossyStoneBricks = new Block[]{
      Blocks.MOSSY_STONE_BRICKS,
      Blocks.INFESTED_MOSSY_STONE_BRICKS,
      Blocks.MOSSY_STONE_BRICK_SLAB,
      Blocks.MOSSY_STONE_BRICK_STAIRS,
      Blocks.MOSSY_STONE_BRICK_WALL
    };

    Block[] mossyCobblestone = new Block[]{
      Blocks.MOSSY_COBBLESTONE,
      Blocks.MOSSY_COBBLESTONE_SLAB,
      Blocks.MOSSY_COBBLESTONE_STAIRS,
      Blocks.MOSSY_COBBLESTONE_WALL,
    };

    List<Block> allMossy = new ArrayList<>();

    if (mossyStoneBrickEnabled.get()) {
      allMossy.addAll(Lists.newArrayList(mossyStoneBricks));
    }

    if (mossyCobblestoneEnabled.get()) {
      allMossy.addAll(Lists.newArrayList(mossyCobblestone));
    }

    // TODO: as we go down in Y level, gradient to VANILLA_COLOR
    final int MOSS_BLOCKS_VANILLA_COLOR = 9551193;

    if (allMossy.size() > 0) {
      for (Block block : allMossy) {
        ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped());
      }

      Minecraft.getInstance().getBlockColors().register((state, reader, pos, color) -> {
        return reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColor.get(0.5D, 1.0D);
      }, allMossy.toArray(new Block[allMossy.size()]));
    }

    if (itemColorEnabled.get()) {
      allMossy.add(Blocks.GRASS_BLOCK);
      allMossy.add(Blocks.GRASS);
      allMossy.add(Blocks.FERN);
      allMossy.add(Blocks.VINE);
      allMossy.add(Blocks.LILY_PAD);
      allMossy.add(Blocks.TALL_GRASS);
      allMossy.add(Blocks.LARGE_FERN);
      allMossy.add(Blocks.OAK_LEAVES);
      allMossy.add(Blocks.SPRUCE_LEAVES);
      allMossy.add(Blocks.BIRCH_LEAVES);
      allMossy.add(Blocks.JUNGLE_LEAVES);
      allMossy.add(Blocks.ACACIA_LEAVES);
      allMossy.add(Blocks.DARK_OAK_LEAVES);

      Minecraft.getInstance().getItemColors().register((stack, color) -> {
        Level reader = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        BlockPos pos = player == null ? null : player.blockPosition();

        return reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColor.get(0.5D, 1.0D);
      }, allMossy.toArray(new Block[0]));
    } else if (allMossy.size() > 0) {
      Minecraft.getInstance().getItemColors().register((stack, color) -> {
        return MOSS_BLOCKS_VANILLA_COLOR;
      }, allMossy.toArray(new Block[0]));
    }
  }

  public static class RunOnceAfterForgeHack {
    private final Runnable runnable;

    private RunOnceAfterForgeHack(Runnable runnable) {
      this.runnable = runnable;
    }

    public static void run(Runnable runnable) {
      MinecraftForge.EVENT_BUS.register(new RunOnceAfterForgeHack(runnable));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
      if (!net.minecraftforge.client.loading.ClientModLoader.isLoading()) {
        this.runnable.run();

        MinecraftForge.EVENT_BUS.unregister(this);
      }
    }
  }
}
