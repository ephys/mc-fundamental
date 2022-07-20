package be.ephys.fundamental;

import be.ephys.cookiecore.config.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.RegistryObject;

public class CraftingTableModule {

  @Config(name = "crafting_tables", description = "Add a crafting table for each vanilla wood variant")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue enabled; // read by JSON recipes

  public static final ResourceLocation CRAFTING_TABLE_TAG = new ResourceLocation("forge", "workbenches");
  public static final TagKey<Block> CRAFTING_TABLE_TAG_WRAPPER = BlockTags.create(CRAFTING_TABLE_TAG);

  private static final Item.Properties CraftingTableItemProperties = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS);

  private static BlockBehaviour.Properties createWood(MaterialColor color) {
    return BlockBehaviour.Properties.of(Material.WOOD, color).strength(2.5F).sound(SoundType.WOOD);
  }

  private static BlockBehaviour.Properties createNetherWood(MaterialColor color) {
    return BlockBehaviour.Properties.of(Material.NETHER_WOOD, color).strength(2.5F).sound(SoundType.WOOD);
  }

  // WARPED

  public static final RegistryObject<Block> WARPED_CRAFTING_TABLE_BLOCK = Mod.BLOCKS.register("warped_crafting_table", () ->
    new CraftingTableBlock(createNetherWood(MaterialColor.WARPED_STEM))
  );

  public static final RegistryObject<Item> WARPED_CRAFTING_TABLE_ITEM = Mod.ITEMS.register("warped_crafting_table", () ->
    new BlockItem(WARPED_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // CRIMSON

  public static final RegistryObject<Block> CRIMSON_CRAFTING_TABLE_BLOCK = Mod.BLOCKS.register("crimson_crafting_table", () ->
    new CraftingTableBlock(createNetherWood(MaterialColor.CRIMSON_STEM))
  );

  public static final RegistryObject<Item> CRIMSON_CRAFTING_TABLE_ITEM = Mod.ITEMS.register("crimson_crafting_table", () ->
    new BlockItem(CRIMSON_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // ACACIA

  public static final RegistryObject<Block> ACACIA_CRAFTING_TABLE_BLOCK = Mod.BLOCKS.register("acacia_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.COLOR_ORANGE))
  );

  public static final RegistryObject<Item> ACACIA_CRAFTING_TABLE_ITEM = Mod.ITEMS.register("acacia_crafting_table", () ->
    new BlockItem(ACACIA_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // BIRCH

  public static final RegistryObject<Block> BIRCH_CRAFTING_TABLE_BLOCK = Mod.BLOCKS.register("birch_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.SAND))
  );

  public static final RegistryObject<Item> BIRCH_CRAFTING_TABLE_ITEM = Mod.ITEMS.register("birch_crafting_table", () ->
    new BlockItem(BIRCH_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // DARK OAK

  public static final RegistryObject<Block> DARK_OAK_CRAFTING_TABLE_BLOCK = Mod.BLOCKS.register("dark_oak_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.COLOR_BROWN))
  );

  public static final RegistryObject<Item> DARK_OAK_CRAFTING_TABLE_ITEM = Mod.ITEMS.register("dark_oak_crafting_table", () ->
    new BlockItem(DARK_OAK_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // JUNGLE

  public static final RegistryObject<Block> JUNGLE_CRAFTING_TABLE_BLOCK = Mod.BLOCKS.register("jungle_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.DIRT))
  );

  public static final RegistryObject<Item> JUNGLE_CRAFTING_TABLE_ITEM = Mod.ITEMS.register("jungle_crafting_table", () ->
    new BlockItem(JUNGLE_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );

  // SPRUCE

  public static final RegistryObject<Block> SPRUCE_CRAFTING_TABLE_BLOCK = Mod.BLOCKS.register("spruce_crafting_table", () ->
    new CraftingTableBlock(createWood(MaterialColor.PODZOL))
  );

  public static final RegistryObject<Item> SPRUCE_CRAFTING_TABLE_ITEM = Mod.ITEMS.register("spruce_crafting_table", () ->
    new BlockItem(SPRUCE_CRAFTING_TABLE_BLOCK.get(), CraftingTableItemProperties)
  );
}
