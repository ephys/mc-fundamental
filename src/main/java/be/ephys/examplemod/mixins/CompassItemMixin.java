package be.ephys.examplemod.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.item.CompassItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompassItem.class)
public class CompassItemMixin {

  @Inject(method = "func_234669_a_", at = @At("RETURN"))
  private void func_234669_a_$setLodestoneName(RegistryKey<World> dimension, BlockPos pos, CompoundNBT nbt, CallbackInfo ci) {
    World world = ServerLifecycleHooks.getCurrentServer().getWorld(dimension);

    if (world == null) {
      return;
    }

    ITextComponent name = getSignName(world, pos);
    setLodestoneName(nbt, name);
  }

  private void setLodestoneName(CompoundNBT nbt, ITextComponent name) {
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

  private ITextComponent getSignName(World world, BlockPos pos) {
    for (Direction face : Direction.Plane.HORIZONTAL) {
      ITextComponent name = getSignNameFromFace(world, pos, face);
      if (name == null) {
        continue;
      }

      return name;
    }

    return null;
  }

  private ITextComponent getSignNameFromFace(World world, BlockPos pos, Direction face) {
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
        allText.append(lineText.deepCopy());
      }
    }

    if (allText == null || allText.getString().equals("")) {
      return null;
    }

    return allText;
  }
}
