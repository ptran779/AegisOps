package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.config.ServerConfig;
import com.github.ptran779.aegisops.config.SkinManager;
import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.extra.FallingHellPod;
import com.github.ptran779.aegisops.network.player.CameraModePacket;
import com.github.ptran779.aegisops.network.player.serverConfigPacket;
import com.github.ptran779.aegisops.player.TaticalCommandProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.config.AgentConfigManager;
import net.minecraftforge.network.PacketDistributor;

import static com.github.ptran779.aegisops.network.PacketHandler.CHANNELS;
import static com.github.ptran779.aegisops.server.EntityInit.FALLING_HELL_POD;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeServerEvent {

  @SubscribeEvent
  public static void onConfigLoad(ServerStartingEvent event) {
    AgentConfigManager.serverGenerateDefault();
  }

  @SubscribeEvent
  public static void deployHellPod(TickEvent.ServerTickEvent event) {
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
    // sync server config so everyone has the same copy of agent config
    if (player instanceof ServerPlayer serverPlayer) {
      // Get the "Long String" from your manager
      String json = AgentConfigManager.getSyncPayload();

      // Send the packet (using your channel instance name 'CHANNELS')
      CHANNELS.send(
          PacketDistributor.PLAYER.with(() -> serverPlayer),
          new serverConfigPacket(json) // Or S2CAgentConfigSyncPacket, whatever you named it
      );
    }

    // 1st time joining get to be delivered in a hell pod :)
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

  // Capabilities stuff
  @SubscribeEvent
  public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
    if(event.getObject() instanceof Player) {
      if(!event.getObject().getCapability(TaticalCommandProvider.TATICAL_COMMAND_CAPABILITY).isPresent()) {
        event.addCapability(new ResourceLocation(AegisOps.MOD_ID, "properties"), new TaticalCommandProvider());
      }
    }
  }
  @SubscribeEvent
  public static void onPlayerCloned(PlayerEvent.Clone event) {
    if(event.isWasDeath()){
      event.getOriginal().getCapability(TaticalCommandProvider.TATICAL_COMMAND_CAPABILITY).ifPresent(oldStore -> {
        event.getOriginal().getCapability(TaticalCommandProvider.TATICAL_COMMAND_CAPABILITY).ifPresent(newStore -> {
          newStore.copyFrom(oldStore);
        });
      });
    }
  }
  @SubscribeEvent
  public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
    event.register(TaticalCommandProvider.class);
  }
}
