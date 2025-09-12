package be.ephys.fundamental.mixins;

import be.ephys.fundamental.biome_trees.BiomeTreeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractTreeGrower.class)
public abstract class AbstractTreeGrowerMixin {
  @Unique
  private final RandomSource mc_fundamental$random = RandomSource.create();

  @Shadow
  @Nullable
  protected abstract ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers);

  @Inject(method = "growTree", at = @At("HEAD"), cancellable = true)
  private void onGrowTree(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
    AbstractTreeGrower treeGrower = (AbstractTreeGrower) (Object) this;

    ResourceKey<ConfiguredFeature<?, ?>> treeKey = this.getConfiguredFeature(this.mc_fundamental$random, false);

    if (treeKey == null && treeGrower instanceof AbstractMegaTreeGrower megaTreeGrower) {
      treeKey = megaTreeGrower.getConfiguredMegaFeature(this.mc_fundamental$random);
    }

    if (treeKey == null) {
      return;
    }

    Holder<ConfiguredFeature<?, ?>> holder = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(treeKey).orElse((Holder.Reference<ConfiguredFeature<?, ?>>) null);
    if (holder == null) {
      return;
    }

    boolean success = BiomeTreeModule.spawnBiomeTree(level, generator, pos, state, random, holder.get());

    if (success) {
      cir.setReturnValue(true);
    }
  }
}
