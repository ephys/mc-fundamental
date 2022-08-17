package be.ephys.fundamental;

import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FundamentalMixinsPlugin implements IMixinConfigPlugin {
  @Override
  public void onLoad(String mixinPackage) {}

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    // Quark implements the same banner limit change, but they forgot BannerDuplicateRecipe.
    // We don't want to cause an issue if Quark is loaded, so we disable the duplicate mixins.
    boolean isQuarkLoaded = FMLLoader.getLoadingModList().getModFileById("quark") != null;
    if (isQuarkLoaded) {
      return !Objects.equals(mixinClassName, "be.ephys.fundamental.mixins.LoomMenuMixin") && !Objects.equals(mixinClassName, "be.ephys.fundamental.mixins.LoomScreenMixin");
    }

    return true;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
