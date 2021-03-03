package be.ephys.examplemod.bound_lodestone;

import be.ephys.examplemod.Mod;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;

import java.util.Random;
import java.util.Set;

public class BoundLodestoneFeature extends Feature<NoFeatureConfig> {

  public static final ResourceLocation STRUCTURE_TEMPLATE_RL = Mod.id("bound_lodestone/overworld");

  public BoundLodestoneFeature(Codec<NoFeatureConfig> codec) {
    super(codec);
  }

  // TODO: nether - BlackStoneReplacementProcessor
  // BlockMosinessProcessor?

  public static Template generateTemplate(ResourceLocation templateRL, ISeedReader world, Random random, BlockPos position) {
//    Rotation rotation = Rotation.randomRotation(random);

    PlacementSettings placementsettings = (new PlacementSettings())
      .setRotation(Rotation.NONE)
//      .func_215223_c(true) // ?
//      .func_237133_d_(true) // ?
//      .setMirror(Mirror.LEFT_RIGHT)
      .setRandom(random)
      .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);

    // Dont cache this as templatemanager already does caching behind the
    // scenes and users might override the file later with datapacks.
    Template template = world.getWorld().getStructureTemplateManager().getTemplate(templateRL);

    if (template == null) {
      Mod.LOGGER.warn(templateRL.toString() + " NTB does not exist!");
      return null;
    }

    BlockPos size = template.getSize();

    BlockPos.Mutable spawnAt = position.toMutable();
    // Centering structure on "position"
    spawnAt.add(-size.getX() / 2, 0, -size.getZ() / 2);

    if (!isTemplateOnGround(world, template, spawnAt)) {
      spawnAt.add(0, -1, 0);
      if (!isTemplateOnGround(world, template, spawnAt)) {
        return null;
      }
    }

    template.func_237152_b_(world, spawnAt, placementsettings, random);

    return template;
  }

  private static boolean isTemplateOnGround(ISeedReader world, Template template, BlockPos spawnAt) {

    // TODO: figure out how to get the list of blocks of the layout 0 in the template
    //   Must be based on PlacementSettings (because rotation / mirror)

    BlockPos size = template.getSize();

    int totalFloor = 0;

    for (int x = 0; x < size.getX(); x++) {
      for (int z = 0; z < size.getZ(); z++) {
        // TODO: check type of blocks is part of terrain (chunkGenerator.getBiomeProvider().getSurfaceBlocks())
        if (world.getBlockState(new BlockPos(spawnAt.getX() + x, spawnAt.getY(), spawnAt.getZ() + z)).isSolid()) {
          totalFloor += 1;
        }

        // debug:
        // world.setBlockState(new BlockPos(spawnAt.getX() + x, spawnAt.getY(), spawnAt.getZ() + z), Blocks.GOLD_BLOCK.getDefaultState(), 0);
      }
    }

    // require at least 80% of full blocks on the floor
    return totalFloor > ((float)size.getX() * (float)size.getZ() * 0.8F);
  }

  @Override
  public boolean func_241855_a(ISeedReader world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, NoFeatureConfig config) {
    System.out.println("GENERATING!");

    // move to top land block below position
    BlockPos.Mutable mutable = new BlockPos.Mutable().setPos(pos);
    mutable.setY(16);
    while (!world.isAirBlock(mutable)) {
      mutable.move(Direction.UP);
    }

    mutable.move(Direction.DOWN);

    System.out.println(mutable);

    BlockState blockAtGenSpot = world.getBlockState(mutable);
    if (!isIn(blockAtGenSpot, chunkGenerator.getBiomeProvider().getSurfaceBlocks())) {
      return false;
    }

    // TODO random chance of spawning
//    // check to make sure spot is valid and not a single block ledge
//    Block block = world.getBlockState(mutable).getBlock();
//    if (isDirt(block) && (!world.isAirBlock(mutable.down()) || !world.isAirBlock(mutable.down(2)))) {

    // Creates the well centered on our spot
    Template template = generateTemplate(STRUCTURE_TEMPLATE_RL, world, random, mutable);

    // TODO: handle BoundLodestone TileEntity.
    //  this.handleDataBlocks(FOREST_WELL_ORE_RL, template, world, random, mutable, Blocks.STONE, ORE_CHANCE);

    return true;
//    }
//
//    return false;
  }

  private static boolean isIn(BlockState blockState, Set<BlockState> blockStates) {
    for (BlockState item : blockStates) {
      if (blockState.isIn(item.getBlock())) {
        return true;
      }
    }

    return false;
  }
}
