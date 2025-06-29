package com.github.ptran779.aegisops.Config;

import com.github.ptran779.aegisops.entity.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.GunTabType;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;


public class AgentConfigManager {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Path CONFIG_DIR = Path.of("config/aegisops/classes");

  // Cache of all loaded class configs
  private static final Map<String, AgentConfig> CLASS_CONFIGS = new HashMap<>();

  // Load all JSON configs in the folder into cache memory
  public static void reloadCache() {
    CLASS_CONFIGS.clear();
    try {
      if (!Files.exists(CONFIG_DIR)) {
        Files.createDirectories(CONFIG_DIR);
      }
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(CONFIG_DIR, "*.json")) {
        for (Path file : stream) {
          String classId = file.getFileName().toString().replace(".json", "");
          try (Reader reader = Files.newBufferedReader(file)) {
            AgentConfig config = GSON.fromJson(reader, AgentConfig.class);
            if (config != null) {
              CLASS_CONFIGS.put(classId, config);
            }
          } catch (IOException e) {
            System.err.println("[AegisOps] Failed to read config for class: " + classId);
            e.printStackTrace();
          }
        }
      }

    } catch (IOException e) {
      System.err.println("[AegisOps] Failed to scan config folder.");
      e.printStackTrace();
    }
  }

  // Make a default file if one doesn’t exist. Use defaultGunTypes to fill in the file
  private static void generateDefaultIfMissing(String classId, List<GunTabType> defaultGunTypes) {
    Path path = CONFIG_DIR.resolve(classId + ".json");
    if (!Files.exists(path)) {
      //gun
      AgentConfig config = new AgentConfig();
      config.allowGuns = getDefaultGuns(defaultGunTypes); // properly assign the default guns
      config.allowMelees = getDefaultMelees(); // for melee if missing

      try {
        Files.createDirectories(CONFIG_DIR); // ensure folder exists
        try (Writer writer = Files.newBufferedWriter(path)) {
          GSON.toJson(config, writer);
//          System.out.println("[AegisOps] Generated default config for class: " + classId);
        }
      } catch (IOException e) {
        System.err.println("[AegisOps] Failed to write default config for: " + classId);
        e.printStackTrace();
      }
      //skin // empty for now
    }
  }

  private static Set<String> getDefaultGuns(List<GunTabType> defaultGunTypes) {
    // Convert defaultGunTypes enum list to lowercase strings for fast lookup
    Set<String> allowedTypes = defaultGunTypes.stream().map(type -> type.name().toLowerCase(Locale.US)).collect(Collectors.toSet());

    // scan entire tacz weapon list and filter
    Set<String> defaultGuns = new HashSet<>();

    TimelessAPI.getAllCommonGunIndex().forEach(entry -> {
      String gunType = entry.getValue().getType();
      if (allowedTypes.contains(gunType)) {defaultGuns.add(entry.getKey().toString());}
    });
    return defaultGuns;
  }

  private static Set<String> getDefaultMelees() {
    Set<String> defaultMelees = new HashSet<>();
    // Swords
    defaultMelees.add("minecraft:wooden_sword");
    defaultMelees.add("minecraft:stone_sword");
    defaultMelees.add("minecraft:iron_sword");
    defaultMelees.add("minecraft:golden_sword");
    defaultMelees.add("minecraft:diamond_sword");
    defaultMelees.add("minecraft:netherite_sword");

    // Axes (vanilla tools but used as weapons)
    defaultMelees.add("minecraft:wooden_axe");
    defaultMelees.add("minecraft:stone_axe");
    defaultMelees.add("minecraft:iron_axe");
    defaultMelees.add("minecraft:golden_axe");
    defaultMelees.add("minecraft:diamond_axe");
    defaultMelees.add("minecraft:netherite_axe");

    return defaultMelees;
  }

  private static AgentConfig getConfig(String classId) {
    AgentConfig out = CLASS_CONFIGS.get(classId);
    if (out == null) {return new AgentConfig();}
    return out;
  }

  // IMPORTANT: make sure all class is register here
  public static void serverGenerateDefault() {
    // take care of generate missing default
    generateDefaultIfMissing("soldier", List.of(GunTabType.PISTOL, GunTabType.RIFLE, GunTabType.SMG));
    generateDefaultIfMissing("sniper", List.of(GunTabType.PISTOL, GunTabType.RIFLE, GunTabType.SNIPER));
    generateDefaultIfMissing("heavy", List.of(GunTabType.PISTOL, GunTabType.SHOTGUN, GunTabType.MG));
    generateDefaultIfMissing("demolition", List.of(GunTabType.PISTOL, GunTabType.SMG));
    generateDefaultIfMissing("medic", List.of(GunTabType.PISTOL, GunTabType.SMG));
    generateDefaultIfMissing("engineer", List.of(GunTabType.PISTOL, GunTabType.RIFLE));
    generateDefaultIfMissing("swordman", List.of(GunTabType.PISTOL, GunTabType.SHOTGUN));

    // load into memory cache
    reloadCache(); // load everything into memory afterward

    // distribute to agent. Purge the cache memory? or since they're refrence, it's fine?
//    printEverything();
    Soldier.updateClassConfig(getConfig("soldier"));
    Sniper.updateClassConfig(getConfig("sniper"));
    Heavy.updateClassConfig(getConfig("heavy"));
    Demolition.updateClassConfig(getConfig("demolition"));
    Medic.updateClassConfig(getConfig("medic"));
    Engineer.updateClassConfig(getConfig("engineer"));
    Swordman.updateClassConfig(getConfig("swordman"));
  }
}
