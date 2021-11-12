package be.ephys.fundamental;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = Mod.EventBusSubscriber.Bus.MOD,
  value = Dist.CLIENT
)
public class Client {

  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    // TODO:
    //  - make "item color respects biome" disableable
    //  - make "mossy cobblestone respects biome" disableable
    //  - make "mossy stone brick respects biome" disableableh
    //  These last two require loading resource pack dynamically:
    //   https://forums.minecraftforge.net/topic/76224-is-there-a-way-to-dynamically-change-vanilla-block-textures/

    // TODO (config?): Make moss color more like the vanilla block based on Y level (the deeper you go the more like vanilla it is).

    Block[] mossyStoneBricks = new Block[]{
      Blocks.MOSSY_STONE_BRICKS,
      Blocks.INFESTED_MOSSY_STONE_BRICKS,
      Blocks.MOSSY_STONE_BRICK_SLAB,
//      Blocks.MOSSY_STONE_BRICK_STAIRS,
//      Blocks.MOSSY_STONE_BRICK_WALL
    };

    Block[] mossyCobblestone = new Block[]{
      Blocks.MOSSY_COBBLESTONE,
      Blocks.MOSSY_COBBLESTONE_SLAB,
//      Blocks.MOSSY_COBBLESTONE_STAIRS,
//      Blocks.MOSSY_COBBLESTONE_WALL,
    };

    List<Block> allMossy = new ArrayList<>();
    allMossy.addAll(Lists.newArrayList(mossyStoneBricks));
    allMossy.addAll(Lists.newArrayList(mossyCobblestone));

    for (Block block : allMossy) {
      RenderTypeLookup.setRenderLayer(block, RenderType.getCutoutMipped());
    }

    Minecraft.getInstance().getBlockColors().register((state, reader, pos, color) -> {
      return reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : GrassColors.get(0.5D, 1.0D);
    }, allMossy.toArray(new Block[allMossy.size()]));

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
  }
}
