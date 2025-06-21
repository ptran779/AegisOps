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
    CHANNELS.registerMessage(id++,AgentActionPacket.class, AgentActionPacket::encode, AgentActionPacket::decode, AgentActionPacket::handle);
  }
}
