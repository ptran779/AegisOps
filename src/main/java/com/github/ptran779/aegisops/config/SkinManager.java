package com.github.ptran779.aegisops.config;

import com.github.ptran779.aegisops.AegisOps;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.text.Normalizer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class SkinManager {
  private static final Minecraft MC = Minecraft.getInstance();
  private static final Path SKIN_DIR = Path.of("config/aegisops/skins");
  private static final String NAMESPACE = AegisOps.MOD_ID+"_dynamic";
  private static final ResourceLocation MISSING_TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/entities/defaultslim.png");

  // Indexed by gender: 0 = male, 1 = female
  private static final Map<String, ResourceLocation> MALE_SKINS = new HashMap<>();
  private static final Map<String, ResourceLocation> FEMALE_SKINS = new HashMap<>();
  private static final Set<ResourceLocation> REGISTERED_TEXTURES = new HashSet<>();

  public static void init() {
    Path maleDir = SKIN_DIR.resolve("male");
    Path femaleDir = SKIN_DIR.resolve("female");

    try {
      Files.createDirectories(maleDir);
      Files.createDirectories(femaleDir);

      // Copy default skins only if the folders were just created (i.e., empty)
      if (Files.list(maleDir).findAny().isEmpty()) {
        copyDefault("/assets/aegisops/textures/entities/defaultwide.png", maleDir.resolve("defaultwide.png"));
      }
      if (Files.list(femaleDir).findAny().isEmpty()) {
        copyDefault("/assets/aegisops/textures/entities/defaultslim.png", femaleDir.resolve("defaultslim.png"));
      }

      //load the dynamic resource
      loadSkinsFromFolder("male", maleDir, MALE_SKINS);
      loadSkinsFromFolder("female", femaleDir, FEMALE_SKINS);

    } catch (IOException e) {
      System.err.println("[AegisOps] Failed to initialize skin directories:");
      e.printStackTrace();
    }
  }

  private static void copyDefault(String internalPath, Path targetPath) {
    try (InputStream in = SkinManager.class.getResourceAsStream(internalPath)) {
      if (in != null) Files.copy(in, targetPath);
    } catch (IOException e) {
      System.err.println("[AegisOps] Failed to copy default skin: " + internalPath);
      e.printStackTrace();
    }
  }

  private static String makeSafeSkinName(String rawFileName) {
    if (rawFileName == null || rawFileName.isEmpty()) return "skin_default";

    // 1. Strip extension if present
    int dotIndex = rawFileName.lastIndexOf('.');
    String base = (dotIndex > 0 ? rawFileName.substring(0, dotIndex) : rawFileName);

    // 2. Lowercase
    base = base.toLowerCase(Locale.ROOT);

    // 3. Normalize Unicode → ASCII
    base = Normalizer.normalize(base, Normalizer.Form.NFKD)
        .replaceAll("\\p{M}", ""); // removes diacritics

    // 4. Replace illegal ResourceLocation characters with _
    base = base.replaceAll("[^a-z0-9._-]", "_");

    // 5. Collapse multiple underscores and trim edges
    base = base.replaceAll("_+", "_")
        .replaceAll("^_|_$", "");

    // 6. Fallback if empty after cleaning
    if (base.isEmpty()) base = "skin";

    // 7. Append short hash to avoid collisions
    String hash = Integer.toHexString(rawFileName.hashCode());
    return base + "_" + hash;
  }

  private static void loadSkinsFromFolder(String genderKey, Path folder, Map<String, ResourceLocation> skinMap) {
    try (Stream<Path> stream = Files.walk(folder)) {
      stream.filter(Files::isRegularFile)
        .filter(p -> p.toString().endsWith(".png"))
        .forEach(path -> {
          String fileName = path.getFileName().toString().replace(".png", "");
          String id = genderKey + "/" + makeSafeSkinName(fileName);
          ResourceLocation rl = new ResourceLocation(NAMESPACE, id);

          try (InputStream in = Files.newInputStream(path)) {
            NativeImage img = NativeImage.read(in);
            DynamicTexture dynTex = new DynamicTexture(img);
            MC.getTextureManager().register(rl, dynTex);  //important: register to mc manager. need to purge at each reload //fixme critical
            skinMap.put(fileName, rl);
            REGISTERED_TEXTURES.add(rl);
          } catch (IOException e) {
            System.err.println("[AegisOps] Failed to load skin image: " + path);
            e.printStackTrace();
          }
        });
    } catch (IOException e) {
      System.err.println("[AegisOps] Failed to walk folder: " + folder);
      e.printStackTrace();
    }
  }

  public static Set<String> getAllSkin(boolean isFemale){
    return isFemale ? FEMALE_SKINS.keySet() : MALE_SKINS.keySet();
  }

  public static ResourceLocation get(boolean slim, String key) {
    Map<String, ResourceLocation> map = slim ? FEMALE_SKINS : MALE_SKINS;
    ResourceLocation rl = map.get(key.toLowerCase());
    if (rl == null) {
      System.err.println("[AegisOps] No skin found for key: " + key + "wtf?");
    }
    return map.getOrDefault(key.toLowerCase(), MISSING_TEXTURE);
  }

  public static void reload() {
    // Unregister old textures (optional since textures are ref-counted internally)
    for (ResourceLocation rl : REGISTERED_TEXTURES) {
      MC.getTextureManager().release(rl); // This tells MC to drop the texture from GPU/memory
    }

    REGISTERED_TEXTURES.clear();
    MALE_SKINS.clear();
    FEMALE_SKINS.clear();
    init();
  }

  public static String renerateRandom(boolean slim) {
    List<String> keys = new ArrayList<>((slim ? FEMALE_SKINS : MALE_SKINS).keySet());
    return keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
  }
}
