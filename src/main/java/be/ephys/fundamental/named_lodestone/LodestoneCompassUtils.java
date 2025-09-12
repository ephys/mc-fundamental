package be.ephys.fundamental.named_lodestone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;

public class LodestoneCompassUtils {
  public static void setLodestoneName(CompoundTag nbt, Component name) {
    if (!nbt.contains("display")) {
      nbt.put("display", new CompoundTag());
    }

    CompoundTag displayNbt = nbt.getCompound("display");

    // => put NBT hasLodestoneName (bool) & clear it when renamed by anvil instead?

    String customName = displayNbt.getString("Name");
    // has a custom name that is not the lodestone one (Anvil name)
    // prevents overriding custom name
    if (!customName.equals("") && !nbt.getString("lodestone_name").equals(customName)) {
      return;
    }

    // remove lodestone name when right clicking an unnamed lodestone
    if (name == null) {
      nbt.remove("lodestone_name");
      nbt.remove("display");

      return;
    }

    // set lodestone name
    String serialized = Component.Serializer.toJson(name);
    displayNbt.putString("Name", serialized);

    nbt.putString("lodestone_name", serialized);
  }

  public static Component getSignName(Level world, BlockPos pos) {
    for (Direction face : Direction.Plane.HORIZONTAL) {
      Component name = getSignNameFromFace(world, pos, face);
      if (name == null) {
        continue;
      }

      return name;
    }

    return null;
  }

  private static Component getSignNameFromFace(Level world, BlockPos pos, Direction face) {
    BlockPos signPos = pos.relative(face);
    BlockState sign = world.getBlockState(signPos);

    if (!(sign.getBlock() instanceof WallSignBlock)) {
      return null;
    }

    Direction facing = sign.getValue(WallSignBlock.FACING);
    if (facing != face) {
      return null;
    }

    BlockEntity te = world.getBlockEntity(signPos);
    if (!(te instanceof SignBlockEntity)) {
      return null;
    }

    SignBlockEntity signTe = (SignBlockEntity) te;
    SignText signText = signTe.getFrontText();

    boolean hasText = false;
    MutableComponent component = Component.empty();
    for (int line = 0; line < SignText.LINES; line++) {
      Component lineMsg = signText.getMessage(line, /* filtered version */ false);
      if (lineMsg.getString().isEmpty()) {
        continue;
      }

      if (hasText) {
        component.append(" ");
      } else {
        hasText = true;
      }

      component.append(lineMsg);
    }

    if (!hasText) {
      return null;
    }

    return component;
  }
}
