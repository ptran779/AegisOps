package com.github.ptran779.aegisops.network;

import com.github.ptran779.aegisops.AegisOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel CHANNELS = NetworkRegistry.newSimpleChannel(new ResourceLocation(AegisOps.MOD_ID, "main"), ()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public static void register() {
    int id = 0;
    CHANNELS.registerMessage(id++, AgentBoolPacket.class, AgentBoolPacket::encode, AgentBoolPacket::decode, AgentBoolPacket::handle);
    CHANNELS.registerMessage(id++, AgentFollowPacket.class, AgentFollowPacket::encode, AgentFollowPacket::decode, AgentFollowPacket::handle);
    CHANNELS.registerMessage(id++, AgentHostilePacket.class, AgentHostilePacket::encode, AgentHostilePacket::decode, AgentHostilePacket::handle);
    CHANNELS.registerMessage(id++, CameraModePacket.class, CameraModePacket::encode, CameraModePacket::decode, CameraModePacket::handle);
  }
}
