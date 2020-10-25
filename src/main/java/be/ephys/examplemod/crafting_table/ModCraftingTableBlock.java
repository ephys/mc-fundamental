package be.ephys.examplemod.crafting_table;

import be.ephys.examplemod.ExampleMod;
import net.minecraft.block.CraftingTableBlock;

public class ModCraftingTableBlock extends CraftingTableBlock {

  public ModCraftingTableBlock(Properties properties, String registryName) {
    super(properties);

    this.setRegistryName(ExampleMod.MODID, registryName);
  }
}
