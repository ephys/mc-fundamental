package be.ephys.examplemod.crafting_table;

import be.ephys.examplemod.Mod;
import net.minecraft.block.CraftingTableBlock;

public class ModCraftingTableBlock extends CraftingTableBlock {

  public ModCraftingTableBlock(Properties properties, String registryName) {
    super(properties);

    this.setRegistryName(Mod.MODID, registryName);
  }
}
