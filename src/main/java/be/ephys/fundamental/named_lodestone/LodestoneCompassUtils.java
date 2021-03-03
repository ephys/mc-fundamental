package be.ephys.fundamental.named_lodestone;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class LodestoneCompassUtils {
  public static void setLodestoneName(CompoundNBT nbt, ITextComponent name) {
    if (!nbt.contains("display")) {
      nbt.put("display", new CompoundNBT());
    }

    CompoundNBT displayNbt = nbt.getCompound("display");

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
    String serialized = ITextComponent.Serializer.toJson(name);
    displayNbt.putString("Name", serialized);

    nbt.putString("lodestone_name", serialized);
  }

  public static ITextComponent getSignName(World world, BlockPos pos) {
    for (Direction face : Direction.Plane.HORIZONTAL) {
      ITextComponent name = getSignNameFromFace(world, pos, face);
      if (name == null) {
        continue;
      }

      return name;
    }

    return null;
  }

  private static ITextComponent getSignNameFromFace(World world, BlockPos pos, Direction face) {
    BlockPos signPos = pos.offset(face);
    BlockState sign = world.getBlockState(signPos);

    if (!(sign.getBlock() instanceof WallSignBlock)) {
      return null;
    }

    Direction facing = sign.get(WallSignBlock.FACING);
    if (facing != face) {
      return null;
    }

    TileEntity te = world.getTileEntity(signPos);
    if (!(te instanceof SignTileEntity)) {
      return null;
    }

    SignTileEntity signTe = (SignTileEntity) te;

    IFormattableTextComponent allText = null;
    for (int line = 0; line < 4; line++) {
      ITextComponent lineText = signTe.getText(line);
      if (lineText.getString().equals("")) {
        continue;
      }

      if (allText == null) {
        allText = lineText.deepCopy(); // .copy
      } else {
        allText.appendString(" ");
        allText.append(lineText.deepCopy());
      }
    }

    if (allText == null || allText.getString().equals("")) {
      return null;
    }

    return allText;
  }
}
