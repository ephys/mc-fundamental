package be.ephys.examplemod;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {

  public static final ResourceLocation CRAFTING_TABLE_TAG = new ResourceLocation(ExampleMod.MODID, "crafting_table");
  public static final ITag.INamedTag<Block> CRAFTING_TABLE_TAG_WRAPPER = BlockTags.makeWrapperTag(CRAFTING_TABLE_TAG.toString());

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

  public static void init() {
    BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
  }

  private static final AbstractBlock.Properties CraftingTableProperties = AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD);
  private static final Item.Properties CraftingTableItemProperties = new Item.Properties().group(ItemGroup.DECORATIONS);

  // WARPED

  public static final RegistryObject<Block> WARPED_CRAFTING_TABLE_BLOCK = BLOCKS.register("warped_crafting_table", () ->
    new CraftingTableBlock(CraftingTableProperties)
  );

  public static final RegistryObject<Item> WARPED_CRAFTING_TABLE_ITEM = ITEMS.register("warped_crafting_table", () ->
    new BlockItem(WARPED_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // CRIMSON

  public static final RegistryObject<Block> CRIMSON_CRAFTING_TABLE_BLOCK = BLOCKS.register("crimson_crafting_table", () ->
    new CraftingTableBlock(CraftingTableProperties)
  );

  public static final RegistryObject<Item> CRIMSON_CRAFTING_TABLE_ITEM = ITEMS.register("crimson_crafting_table", () ->
    new BlockItem(CRIMSON_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );
}
