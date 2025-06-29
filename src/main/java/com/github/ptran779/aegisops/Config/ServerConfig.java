package com.github.ptran779.aegisops.Config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ServerConfig {
    public static ForgeConfigSpec.IntValue CLUSTER_SIZE_MIN;
    public static ForgeConfigSpec.IntValue CLUSTER_SIZE_MAX;
    public static ForgeConfigSpec.IntValue MIN_SPAWN_DISTANCE;
    public static ForgeConfigSpec.IntValue MAX_SPAWN_DISTANCE;
    public static ForgeConfigSpec.IntValue SPAWN_EVENT_PERIOD;
    public static ForgeConfigSpec.DoubleValue CHANCE_TO_SPAWN;
    public static ForgeConfigSpec.IntValue DROP_POD_DELAY_OPEN;

    public static void register() {
        registerCommonConfig();
    }

    private static void registerCommonConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Drop Pod Settings").push("droppod");

        CLUSTER_SIZE_MIN = builder
            .comment("Min number of drop pods spawned in a single event")
            .defineInRange("clusterSizeMin", 2, 0, 64);

        CLUSTER_SIZE_MAX = builder
            .comment("Max number of drop pods spawned in a single event")
            .defineInRange("clusterSizeMax", 5, 0, 64);

        DROP_POD_DELAY_OPEN = builder
            .comment("How many tick after drop land to automatically open? ")
            .defineInRange("DropPodDelayOpen", 3000, -1, Integer.MAX_VALUE);

        SPAWN_EVENT_PERIOD = builder
            .comment("How many ticks between automatic drop pod spawn events (1 day = 24000 ticks)")
            .defineInRange("SpawnEventPeriod", 24000, 80, Integer.MAX_VALUE);

        CHANCE_TO_SPAWN = builder
            .comment("Chance to trigger agent drop pod deployment")
            .defineInRange("ChanceToSpawn", 0.25f, 0f, 1f);

        MIN_SPAWN_DISTANCE = builder
            .comment("Min distance from player where drop pods can spawn")
            .defineInRange("minSpawnDistance", 64, 0, Integer.MAX_VALUE);

        MAX_SPAWN_DISTANCE = builder
            .comment("Max distance from player where drop pods can spawn")
            .defineInRange("maxSpawnDistance", 128, 16, Integer.MAX_VALUE);

        builder.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, builder.build());
    }
}
