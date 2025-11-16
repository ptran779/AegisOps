package com.github.ptran779.aegisops.network;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.network.Agent.*;
import com.github.ptran779.aegisops.network.player.CameraModePacket;
import com.github.ptran779.aegisops.network.player.KeyBindPacket;
import com.github.ptran779.aegisops.network.render.EntityRenderPacket;
import com.github.ptran779.aegisops.network.render.StructureRenderPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel CHANNELS = NetworkRegistry.newSimpleChannel(new ResourceLocation(AegisOps.MOD_ID, "main"), ()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public static void register() {
    int id = 0;
    CHANNELS.registerMessage(id++, AgentDismissPacket.class, AgentDismissPacket::encode, AgentDismissPacket::decode, AgentDismissPacket::handle);
    CHANNELS.registerMessage(id++, AgentSpecialPacket.class, AgentSpecialPacket::encode, AgentSpecialPacket::decode, AgentSpecialPacket::handle);
    CHANNELS.registerMessage(id++, AgentFollowPacket.class, AgentFollowPacket::encode, AgentFollowPacket::decode, AgentFollowPacket::handle);
    CHANNELS.registerMessage(id++, AgentHostilePacket.class, AgentHostilePacket::encode, AgentHostilePacket::decode, AgentHostilePacket::handle);
    CHANNELS.registerMessage(id++, ChangeSkinPacket.class, ChangeSkinPacket::encode, ChangeSkinPacket::decode, ChangeSkinPacket::handle);

    CHANNELS.registerMessage(id++, CameraModePacket.class, CameraModePacket::encode, CameraModePacket::decode, CameraModePacket::handle);
    CHANNELS.registerMessage(id++, EntityRenderPacket.class, EntityRenderPacket::encode, EntityRenderPacket::decode, EntityRenderPacket::handle);
    CHANNELS.registerMessage(id++, StructureRenderPacket.class, StructureRenderPacket::encode, StructureRenderPacket::decode, StructureRenderPacket::handle);
    CHANNELS.registerMessage(id++, KeyBindPacket.class, KeyBindPacket::encode, KeyBindPacket::decode, KeyBindPacket::handle);
  }
}
