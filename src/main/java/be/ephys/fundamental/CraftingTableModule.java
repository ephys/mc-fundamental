package be.ephys.fundamental;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftingTableModule {

  public static final ResourceLocation CRAFTING_TABLE_TAG = new ResourceLocation("forge", "workbenches");
  public static final ITag.INamedTag<Block> CRAFTING_TABLE_TAG_WRAPPER = BlockTags.makeWrapperTag(CRAFTING_TABLE_TAG.toString());

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Mod.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Mod.MODID);

  public static void init() {
    BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
  }

  private static final Item.Properties CraftingTableItemProperties = new Item.Properties().group(ItemGroup.DECORATIONS);

  private static AbstractBlock.Properties createWood(MaterialColor color) {
    return AbstractBlock.Properties.create(Material.WOOD, color).hardnessAndResistance(2.5F).sound(SoundType.WOOD);
  }

  private static AbstractBlock.Properties createNetherWood(MaterialColor color) {
    return AbstractBlock.Properties.create(Material.NETHER_WOOD, color).hardnessAndResistance(2.5F).sound(SoundType.WOOD);
  }

  // WARPED

  public static final RegistryObject<Block> WARPED_CRAFTING_TABLE_BLOCK = BLOCKS.register("warped_crafting_table", () ->
    new CraftingTableBlock(createNetherWood(MaterialColor.WARPED_STEM))
  );

  public static final RegistryObject<Item> WARPED_CRAFTING_TABLE_ITEM = ITEMS.register("warped_crafting_table", () ->
    new BlockItem(WARPED_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // CRIMSON

  public static final RegistryObject<Block> CRIMSON_CRAFTING_TABLE_BLOCK = BLOCKS.register("crimson_crafting_table", () ->
    new CraftingTableBlock(createNetherWood(MaterialColor.CRIMSON_STEM))
  );

  public static final RegistryObject<Item> CRIMSON_CRAFTING_TABLE_ITEM = ITEMS.register("crimson_crafting_table", () ->
    new BlockItem(CRIMSON_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // ACACIA

  public static final RegistryObject<Block> ACACIA_CRAFTING_TABLE_BLOCK = BLOCKS.register("acacia_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.ADOBE))
  );

  public static final RegistryObject<Item> ACACIA_CRAFTING_TABLE_ITEM = ITEMS.register("acacia_crafting_table", () ->
    new BlockItem(ACACIA_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // BIRCH

  public static final RegistryObject<Block> BIRCH_CRAFTING_TABLE_BLOCK = BLOCKS.register("birch_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.SAND))
  );

  public static final RegistryObject<Item> BIRCH_CRAFTING_TABLE_ITEM = ITEMS.register("birch_crafting_table", () ->
    new BlockItem(BIRCH_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // DARK OAK

  public static final RegistryObject<Block> DARK_OAK_CRAFTING_TABLE_BLOCK = BLOCKS.register("dark_oak_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.BROWN))
  );

  public static final RegistryObject<Item> DARK_OAK_CRAFTING_TABLE_ITEM = ITEMS.register("dark_oak_crafting_table", () ->
    new BlockItem(DARK_OAK_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // JUNGLE

  public static final RegistryObject<Block> JUNGLE_CRAFTING_TABLE_BLOCK = BLOCKS.register("jungle_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.DIRT))
  );

  public static final RegistryObject<Item> JUNGLE_CRAFTING_TABLE_ITEM = ITEMS.register("jungle_crafting_table", () ->
    new BlockItem(JUNGLE_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // SPRUCE

  public static final RegistryObject<Block> SPRUCE_CRAFTING_TABLE_BLOCK = BLOCKS.register("spruce_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.OBSIDIAN))
  );

  public static final RegistryObject<Item> SPRUCE_CRAFTING_TABLE_ITEM = ITEMS.register("spruce_crafting_table", () ->
    new BlockItem(SPRUCE_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );
}
