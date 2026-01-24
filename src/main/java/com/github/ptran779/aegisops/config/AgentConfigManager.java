package com.github.ptran779.aegisops.config;

import com.github.ptran779.aegisops.entity.agent.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.GunTabType;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class AgentConfigManager {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Path CONFIG_DIR = Path.of("config/aegisops/classes");

  // Cache of all loaded class configs
  private static final Map<String, AgentConfig> CLASS_CONFIGS = new HashMap<>();

  // Load all JSON configs in the folder into cache memory
  // fixme future admin command to reload server config without restart everything
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
    Set<String> defaultMeleeTags = new HashSet<>();
    defaultMeleeTags.add("#minecraft:swords");
    defaultMeleeTags.add("#forge:tools/swords");
    defaultMeleeTags.add("#minecraft:axes");
    defaultMeleeTags.add("#forge:tools/axes");
    return defaultMeleeTags;  // empty set since default allow all tag
  }

  // Make a default file if one doesn’t exist. Use defaultGunTypes to fill in the file
  private static void loadOrGenerateClassConfig(String classId, List<GunTabType> defaultGunTypes, int maxVirtAmmo, int chargePerAmmo) {
    Path path = CONFIG_DIR.resolve(classId + ".json");

    // Generate default
    AgentConfig defaultConfig = new AgentConfig();
    defaultConfig.allowGuns = getDefaultGuns(defaultGunTypes);
    defaultConfig.allowMelees = getDefaultMelees();
    defaultConfig.maxVirtualAmmo = maxVirtAmmo;
    defaultConfig.chargePerAmmo = chargePerAmmo;
    defaultConfig.defaultMaleSkin = "defaultwide";
    defaultConfig.defaultFemaleSkin = "defaultslim";

    AgentConfig result;

    try {
      Files.createDirectories(CONFIG_DIR);

      if (!Files.exists(path)) {
        try (Writer writer = Files.newBufferedWriter(path)) {
          GSON.toJson(defaultConfig, writer);
          result = defaultConfig;
        }
      } else {
        try (Reader reader = Files.newBufferedReader(path)) {
          AgentConfig loaded = GSON.fromJson(reader, AgentConfig.class);
          if (loaded != null && loaded.isValid()) {
            result = loaded;
          } else {
            System.err.println("[AegisOps] Invalid or incomplete config for " + classId + ", regenerating...");
            try (Writer writer = Files.newBufferedWriter(path)) {
              GSON.toJson(defaultConfig, writer);
              result = defaultConfig;
            }
          }
        }
      }
    } catch (IOException e) {
      System.err.println("[AegisOps] Failed to load or write config for: " + classId);
      e.printStackTrace();
      result = defaultConfig; // fallback to something valid
    }

    CLASS_CONFIGS.put(classId, result);
  }

  private static AgentConfig getConfig(String classId) {
    AgentConfig out = CLASS_CONFIGS.get(classId);
    if (out == null) {return new AgentConfig();}
    return out;
  }

  // SERVER SIDE: Serializes the entire loaded config map into a JSON string.
  public static String getSyncPayload() {
    // If cache is empty, try to reload it first
    if (CLASS_CONFIGS.isEmpty()) {
      reloadCache();
    }
    return GSON.toJson(CLASS_CONFIGS);
  }

  //CLIENT SIDE: deserializes the JSON and updates the local Agent classes.
  public static void handleSync(String json) {
    try {
      Type type = new TypeToken<Map<String, AgentConfig>>(){}.getType();
      Map<String, AgentConfig> receivedData = GSON.fromJson(json, type);

      if (receivedData != null) {
        CLASS_CONFIGS.clear();
        CLASS_CONFIGS.putAll(receivedData);

        // Push the new data to the actual Entity classes
        distributeConfigs();

        System.out.println("[AegisOps] Client successfully synced " + CLASS_CONFIGS.size() + " agent class configs.");
      }
    } catch (Exception e) {
      System.err.println("[AegisOps] Failed to parse sync packet!");
      e.printStackTrace();
    }
  }

  /**
   * SHARED: Pushes the cached config data to the static fields in Agent classes.
   * Call this after loading from disk (Server) OR after receiving packet (Client).
   */
  private static void distributeConfigs() {
    Soldier.updateClassConfig(getConfig("soldier"));
    Sniper.updateClassConfig(getConfig("sniper"));
    Heavy.updateClassConfig(getConfig("heavy"));
    Demolition.updateClassConfig(getConfig("demolition"));
    Medic.updateClassConfig(getConfig("medic"));
    Engineer.updateClassConfig(getConfig("engineer"));
    Swordman.updateClassConfig(getConfig("swordman"));
  }

  // Refactored server startup method to use the shared distribute method
  public static void serverGenerateDefault() {
    // 1. Generate files if missing
    loadOrGenerateClassConfig("soldier", List.of(GunTabType.PISTOL, GunTabType.RIFLE, GunTabType.SMG), 100, 1);
    loadOrGenerateClassConfig("sniper", List.of(GunTabType.PISTOL, GunTabType.RIFLE, GunTabType.SNIPER), 50, 3);
    loadOrGenerateClassConfig("heavy", List.of(GunTabType.PISTOL, GunTabType.SHOTGUN, GunTabType.MG), 200, 2);
    loadOrGenerateClassConfig("demolition", List.of(GunTabType.PISTOL, GunTabType.SMG), 100, 1);
    loadOrGenerateClassConfig("medic", List.of(GunTabType.PISTOL, GunTabType.SMG), 100, 1);
    loadOrGenerateClassConfig("engineer", List.of(GunTabType.PISTOL, GunTabType.RIFLE), 100, 1);
    loadOrGenerateClassConfig("swordman", List.of(GunTabType.PISTOL, GunTabType.SHOTGUN), 50, 2);

    // 2. Distribute to classes
    distributeConfigs();
  }
}
