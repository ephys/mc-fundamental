package be.ephys.fundamental.moss;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.ExtraModResourcePack;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GrassColors;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
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
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = Mod.EventBusSubscriber.Bus.MOD,
  value = Dist.CLIENT
)
public class MossModule {

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
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue mossyStoneBrickEnabled;

  @Config(
    name = "mossy_cobblestone_follows_biome_colors",
    description = "Makes the moss on mossy cobblestone change color based on the biome."
      + "\nThis feature adds a resource pack to modify the Mossy Cobblestone models."
      + "\nDue to a limitation in Forge, the game will reload resource packs the first time this mod is added (and if this config changes), leading to a longer initial loading time. Sorry.",
    side = ModConfig.Type.CLIENT
  )
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue mossyCobblestoneEnabled;

  public static final String MOSSY_COBBLESTONE_PACK_ID = "fundamental:mossy_cobblestone";
  public static final String MOSSY_STONE_BRICK_PACK_ID = "fundamental:mossy_stone_bricks";

  public static void init() {
    ResourcePackList resourcePackList = Minecraft.getInstance().getResourcePackList();
    resourcePackList.addPackFinder(MossModule::addPack);
  }

  private static void addPack(Consumer<ResourcePackInfo> resourcePackInfoConsumer, ResourcePackInfo.IFactory factory) {
    Supplier<IResourcePack> mossyCobblestonePackSupplier = ExtraModResourcePack.createSupplier(
      be.ephys.fundamental.Mod.MODID,
      "fundamental_mossy_cobblestone"
    );

    resourcePackInfoConsumer.accept(factory.create(
      MOSSY_COBBLESTONE_PACK_ID,
      /* always enabled */ false,
      mossyCobblestonePackSupplier,
      /* resourcePack */ mossyCobblestonePackSupplier.get(),
      new PackMetadataSection(
        new TranslationTextComponent("fundamental.pack.mossy_cobblestone.description"),
      6),
      ResourcePackInfo.Priority.TOP,
      IPackNameDecorator.PLAIN
    ));

    Supplier<IResourcePack> mossyStoneBricksPackSupplier = ExtraModResourcePack.createSupplier(
      be.ephys.fundamental.Mod.MODID,
      "fundamental_mossy_stone_bricks"
    );

    resourcePackInfoConsumer.accept(factory.create(
      MOSSY_STONE_BRICK_PACK_ID,
      /* always enabled */ false,
      mossyStoneBricksPackSupplier,
      /* resourcePack */ mossyStoneBricksPackSupplier.get(),
      new PackMetadataSection(
        new TranslationTextComponent("fundamental.pack.mossy_stone_bricks.description"),
        6),
      ResourcePackInfo.Priority.TOP,
      IPackNameDecorator.PLAIN
    ));
  }

  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    Minecraft minecraft = event.getMinecraftSupplier().get();
    ResourcePackList resourcePackList = minecraft.getResourcePackList();

    List<String> enabledPacks = resourcePackList.getEnabledPacks()
      .stream().map(ResourcePackInfo::getName)
      .collect(Collectors.toList());

    List<String> addedPackIds = new ArrayList<>();
    List<String> removedPackIds = new ArrayList<>();

    boolean mossyCobblePackEnabled = enabledPacks.contains(MOSSY_COBBLESTONE_PACK_ID);
    if (mossyCobblestoneEnabled.get()) {
      if (!mossyCobblePackEnabled) {
        addedPackIds.add(MOSSY_COBBLESTONE_PACK_ID);
      }
    } else {
      if (mossyCobblePackEnabled) {
        removedPackIds.add(MOSSY_COBBLESTONE_PACK_ID);
      }
    }

    boolean mossyStoneBricksPackEnabled = enabledPacks.contains(MOSSY_STONE_BRICK_PACK_ID);
    if (mossyStoneBrickEnabled.get()) {
      if (!mossyStoneBricksPackEnabled) {
        addedPackIds.add(MOSSY_STONE_BRICK_PACK_ID);
      }
    } else {
      if (mossyStoneBricksPackEnabled) {
        removedPackIds.add(MOSSY_STONE_BRICK_PACK_ID);
      }
    }

    boolean changed = mossyCobblestoneEnabled.get() != mossyCobblePackEnabled
      || mossyStoneBrickEnabled.get() != mossyStoneBricksPackEnabled;

    if (changed) {
      int modResourcesIndex = enabledPacks.indexOf("mod_resources");
      if (modResourcesIndex == -1) {
        throw new RuntimeException("mod_resources pack is not loaded?");
      }

      enabledPacks.addAll(modResourcesIndex + 1, addedPackIds);
      enabledPacks.removeAll(removedPackIds);

      resourcePackList.setEnabledPacks(enabledPacks);
      minecraft.reloadResources();

      RunOnceAfterForgeHack.run(() -> {
        GameSettings gameSettings = minecraft.gameSettings;
        gameSettings.resourcePacks.clear();
        gameSettings.incompatibleResourcePacks.clear();
        for(ResourcePackInfo resourcepackinfo : resourcePackList.getEnabledPacks()) {
          if (!resourcepackinfo.isOrderLocked()) {
            gameSettings.resourcePacks.add(resourcepackinfo.getName());
            if (!resourcepackinfo.getCompatibility().isCompatible()) {
              gameSettings.incompatibleResourcePacks.add(resourcepackinfo.getName());
            }
          }
        }

        gameSettings.saveOptions();
      });
    }

    // TODO (config?): Make moss color more like the vanilla block based on Y level (the deeper you go the more like vanilla it is).

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
        RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped());
      }

      Minecraft.getInstance().getBlockColors().register((state, reader, pos, color) -> {
        return reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : GrassColors.get(0.5D, 1.0D);
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
        World reader = Minecraft.getInstance().world;
        PlayerEntity player = Minecraft.getInstance().player;
        BlockPos pos = player == null ? null : player.getPosition();

        return reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : GrassColors.get(0.5D, 1.0D);
      }, allMossy.toArray(new Block[allMossy.size()]));
    } else if (allMossy.size() > 0) {
      Minecraft.getInstance().getItemColors().register((stack, color) -> {
        return MOSS_BLOCKS_VANILLA_COLOR;
      }, allMossy.toArray(new Block[allMossy.size()]));
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
      if (!net.minecraftforge.fml.client.ClientModLoader.isLoading()) {
        this.runnable.run();

        MinecraftForge.EVENT_BUS.unregister(this);
      }
    }
  }
}
