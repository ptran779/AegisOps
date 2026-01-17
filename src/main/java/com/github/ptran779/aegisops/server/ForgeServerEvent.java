package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.Config.MlModelManager;
import com.github.ptran779.aegisops.Config.ServerConfig;
import com.github.ptran779.aegisops.Config.SkinManager;
import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.brain.api.BrainInfer;
import com.github.ptran779.aegisops.entity.extra.FallingHellPod;
import com.github.ptran779.aegisops.network.CameraModePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.Config.AgentConfigManager;
import net.minecraftforge.network.PacketDistributor;

import java.util.Arrays;

import static com.github.ptran779.aegisops.network.PacketHandler.CHANNELS;
import static com.github.ptran779.aegisops.server.EntityInit.FALLING_HELL_POD;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeServerEvent {
  public static BrainInfer BRAIN_INFER = null;
  public Thread workerThread;

  @SubscribeEvent
  public static void onServerStarting(ServerStartingEvent event) {
    AgentConfigManager.serverGenerateDefault();
    BRAIN_INFER = new BrainInfer();
    BRAIN_INFER.start();
//    System.out.println("[AegisOps] BrainInfer started");
    SkinManager.reload();
  }

  @SubscribeEvent
  public static void onServerStopping(ServerStoppingEvent event) {
    if (BRAIN_INFER != null) {
      BRAIN_INFER.stop();   // stops loop + interrupts thread
      BRAIN_INFER = null;
//      System.out.println("[AegisOps] BrainInfer stopped");
    }

    MlModelManager.cleanAll();
  }

  @SubscribeEvent
  public static void processAgentBehavior(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) return;
    ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);  // this level should always run
    if (level == null) return;
    if (level.getGameTime() % 20 != 0) return;
    while (BRAIN_INFER.resultQueue.peek() != null) {
      BrainInfer.resultPayload payload = BRAIN_INFER.resultQueue.poll();
//      if (payload.agent != null && payload.agent.isAlive()) {
      System.out.println("Agent " + payload.agentUUID + " got a behavior update");
      System.out.println(Arrays.toString(payload.decision));
    }
  }


  @SubscribeEvent
  public static void deployHellPod(TickEvent.ServerTickEvent event) {  // maybe swap to day/night time trigger fixme
    if (event.phase != TickEvent.Phase.END) return;
    // This is guaranteed to be server-side already
    ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
    if (level == null) return;
    if (level.getGameTime() % ServerConfig.SPAWN_EVENT_PERIOD.get() != 0) return;
    // Spawn event
    for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()){
      if (level.random.nextDouble() >= ServerConfig.CHANCE_TO_SPAWN.get()) continue;

      // pick spawning location
      double angle = level.random.nextDouble() * 2 * Math.PI;
      double distance = Mth.nextDouble(level.random, ServerConfig.MIN_SPAWN_DISTANCE.get(), ServerConfig.MAX_SPAWN_DISTANCE.get());
      double centerX = player.getX() + Math.cos(angle) * distance;
      double centerZ = player.getZ() + Math.sin(angle) * distance;
      double centerY = level.getMaxBuildHeight() - 1;
      // Roll how many pods to spawn
      int min = ServerConfig.CLUSTER_SIZE_MIN.get();
      int max = ServerConfig.CLUSTER_SIZE_MAX.get();
      int clusterSize = Mth.nextInt(level.random, min, max);
      for (int i=0; i<clusterSize; i++) {
        // extra offset for spread
        double offsetX = centerX + (level.random.nextDouble() - 0.5) * 20;
        double offsetZ = centerZ + (level.random.nextDouble() - 0.5) * 20;
        Utils.summonReinforcement(offsetX, centerY, offsetZ, level);
      }
    }
  }


  //Player deployment on world join first time
  @SubscribeEvent
  public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    Player player = event.getEntity();
    CompoundTag persistentData = player.getPersistentData();
    CompoundTag data;

    if (!persistentData.contains(Player.PERSISTED_NBT_TAG)) {
      data = new CompoundTag();
      persistentData.put(Player.PERSISTED_NBT_TAG, data);
    } else {
      data = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
    }

    if (!data.getBoolean("hasJoinedBefore")) {
      data.putBoolean("hasJoinedBefore", true);

      // 🚀 This is the first join!
      player.sendSystemMessage(Component.literal("Welcome to Aegis, Survivor."));
      // spawn pod, play sound, set tags, etc.
      FallingHellPod pod = new FallingHellPod(FALLING_HELL_POD.get(), player.level());
      pod.setPos(player.getX(), player.level().getMaxBuildHeight()-1, player.getZ());
      player.level().addFreshEntity(pod);
      player.startRiding(pod, true);
      CHANNELS.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new CameraModePacket());
    }
  }
}
