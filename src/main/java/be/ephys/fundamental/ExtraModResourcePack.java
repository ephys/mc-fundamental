package be.ephys.fundamental;

import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResourcePack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ExtraModResourcePack {
  public static IResourcePack create(String modId, String pathToPackInJar) {
    ModFile modFile = ModList.get().getModFileById(modId).getFile();
    Path filePath = modFile.getFilePath();

    File file = filePath.toFile();
    if (!file.isDirectory()) {
      // TODO: return ModFilePack
      throw new RuntimeException("TODO: return ModFilePack");
    }

    File containedPack = new File(file, pathToPackInJar);
    System.out.println(containedPack.getAbsolutePath());

    if (!containedPack.isDirectory()) {
      return new FilePack(containedPack);
    }

    return new FolderPack(containedPack);
  }

  public static Supplier<IResourcePack> createSupplier(String modId, String pathToPackInJar) {
    return () -> create(modId, pathToPackInJar);
  }
}
