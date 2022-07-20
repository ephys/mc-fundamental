package be.ephys.fundamental;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtraModResourcePack {
  public static PackResources create(String modId, String pathToPackInJar) {
    IModFile modFile = ModList.get().getModFileById(modId).getFile();
    Path filePath = modFile.getFilePath();

    File file = filePath.toFile();
    if (!file.isDirectory()) {
      return new ExtraModResourcePack.ModFilePack(file, pathToPackInJar);
    }

    File containedPack = new File(file, pathToPackInJar);

    if (!containedPack.isDirectory()) {
      return new FilePackResources(containedPack);
    }

    return new FolderPackResources(containedPack);
  }

  public static Supplier<PackResources> createSupplier(String modId, String pathToPackInJar) {
    return () -> create(modId, pathToPackInJar);
  }

  public static class ModFilePack extends AbstractPackResources {
    public static final Splitter PATH_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
    private final String inJarPath;
    private ZipFile zipFile;

    @Override
    public String getName() {
      return Iterators.getLast(PATH_SPLITTER.split(inJarPath).iterator());
    }

    public ModFilePack(File fileIn, String inJarPath) {
      super(fileIn);

      this.inJarPath = inJarPath;
    }

    private ZipFile getResourcePackZipFile() throws IOException {
      if (this.zipFile == null) {
        this.zipFile = new ZipFile(this.file);
      }

      return this.zipFile;
    }

    protected InputStream getResource(String resourcePath) throws IOException {
      ZipFile zipfile = this.getResourcePackZipFile();
      ZipEntry zipentry = zipfile.getEntry(inJarPath + "/" + resourcePath);
      if (zipentry == null) {
        throw new ResourcePackFileNotFoundException(this.file, resourcePath);
      } else {
        return zipfile.getInputStream(zipentry);
      }
    }

    public boolean hasResource(String resourcePath) {
      try {
        return this.getResourcePackZipFile().getEntry(inJarPath + "/" + resourcePath) != null;
      } catch (IOException ioexception) {
        return false;
      }
    }

    public Set<String> getNamespaces(PackType type) {
      ZipFile zipfile;
      try {
        zipfile = this.getResourcePackZipFile();
      } catch (IOException ioexception) {
        return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
      Set<String> set = Sets.newHashSet();

      while(enumeration.hasMoreElements()) {
        ZipEntry zipentry = enumeration.nextElement();
        String s = zipentry.getName();

        if (!s.startsWith(this.inJarPath)) {
          continue;
        }

        s = s.substring(this.inJarPath.length() + 1);

        if (s.startsWith(type.getDirectory() + "/")) {
          List<String> list = Lists.newArrayList(PATH_SPLITTER.split(s));
          if (list.size() > 1) {
            String s1 = list.get(1);
            if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
              set.add(s1);
            } else {
              this.logWarning(s1);
            }
          }
        }
      }

      return set;
    }

    protected void logWarning(String p_10231_) {
      Mod.LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", p_10231_, this.file);
    }

    protected void finalize() throws Throwable {
      this.close();
      super.finalize();
    }

    public void close() {
      if (this.zipFile != null) {
        IOUtils.closeQuietly((Closeable)this.zipFile);
        this.zipFile = null;
      }

    }

    public Collection<ResourceLocation> getResources(PackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
      ZipFile zipfile;
      try {
        zipfile = this.getResourcePackZipFile();
      } catch (IOException ioexception) {
        return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
      List<ResourceLocation> list = Lists.newArrayList();
      // <assets|data>/<models>/...
      String s = this.inJarPath + "/" + type.getDirectory() + "/" + namespaceIn + "/";
      String s1 = s + pathIn + "/";

      while(enumeration.hasMoreElements()) {
        ZipEntry zipentry = enumeration.nextElement();
        if (zipentry.isDirectory()) {
          continue;
        }

        String filePath = zipentry.getName();
        if (filePath.endsWith(".mcmeta") || !filePath.startsWith(s1)) {
          continue;
        }

        String resourceName = filePath.substring(s.length());
        String[] astring = resourceName.split("/");
        if (astring.length >= maxDepthIn + 1 && filterIn.test(astring[astring.length - 1])) {
          list.add(new ResourceLocation(namespaceIn, resourceName));
        }
      }

      return list;
    }
  }
}
