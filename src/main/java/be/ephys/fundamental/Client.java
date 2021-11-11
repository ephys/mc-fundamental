package be.ephys.fundamental;

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

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = Mod.EventBusSubscriber.Bus.MOD,
  value = Dist.CLIENT
)
public class Client {
  @SubscribeEvent
  public static void setupClient(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(Blocks.MOSSY_COBBLESTONE, RenderType.getCutoutMipped());

    Minecraft.getInstance().getBlockColors().register((state, reader, pos, color) -> {
      return reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : GrassColors.get(0.5D, 1.0D);
    }, Blocks.MOSSY_COBBLESTONE);

    Minecraft.getInstance().getItemColors().register((stack, color) -> {
      World reader = Minecraft.getInstance().world;
      PlayerEntity player = Minecraft.getInstance().player;
      BlockPos pos = player == null ? null : player.getPosition();

      return reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : GrassColors.get(0.5D, 1.0D);
    }, Blocks.MOSSY_COBBLESTONE, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
  }
}
