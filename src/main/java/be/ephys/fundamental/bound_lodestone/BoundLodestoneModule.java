package be.ephys.fundamental.bound_lodestone;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.Mod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD
)
public class BoundLodestoneModule {
  @Config(name = "lodestone.bound_lodestone", description = "Adds a lodestone proxy that when used sets your compass to another lodestone.")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue enabled;

  public static final RegistryObject<Block> BOUND_LODESTONE = Mod.BLOCKS.register("bound_lodestone", () ->
    new BoundLodestoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LODESTONE))
  );

  public static final RegistryObject<Item> BOUND_LODESTONE_ITEM = Mod.ITEMS.register("bound_lodestone", () ->
    new BlockItem(BOUND_LODESTONE.get(), new Item.Properties())
  );

  public static final RegistryObject<BlockEntityType<BoundLodestoneBlockEntity>> BOUND_LODESTONE_TE_TYPE = Mod.BLOCK_ENTITY_TYPES.register("bound_lodestone", () ->
    BlockEntityType.Builder.of(BoundLodestoneBlockEntity::new, BOUND_LODESTONE.get()).build(null)
  );

  @SubscribeEvent
  public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
      event.accept(BOUND_LODESTONE_ITEM.get());
    }
  }
}
