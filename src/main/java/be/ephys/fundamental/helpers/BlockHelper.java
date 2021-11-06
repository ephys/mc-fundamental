package be.ephys.fundamental.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.state.Property;

public class BlockHelper {
  public static BlockState assignBlockState(BlockState overwritten, BlockState overwitting) {
    for (Property prop : overwitting.getProperties()) {
      if (overwritten.hasProperty(prop)) {
        overwritten = overwritten.with(prop, overwitting.get(prop));
      }
    }

    return overwritten;
  }
}
