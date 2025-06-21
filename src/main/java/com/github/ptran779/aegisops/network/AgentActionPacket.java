package com.github.ptran779.aegisops.network;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AgentActionPacket {
  private final int entityId;
  private final AgentCommandType cType;
  private final boolean payload;          //action payload -- expanse me if need more complex data communication
  private final String optinalData;

  public AgentActionPacket(int entityId, AgentCommandType cType, boolean flag, String optinalData){
    this.entityId = entityId;
    this.cType = cType;
    this.payload = flag;
    this.optinalData = optinalData;
  }

  public AgentActionPacket(int entityId, AgentCommandType cType, boolean flag){
    this(entityId, cType, flag, "");
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeEnum(cType);
    buf.writeBoolean(payload);
    buf.writeUtf(optinalData);
  }

  public static AgentActionPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    AgentCommandType cType = buf.readEnum(AgentCommandType.class);
    boolean payload = buf.readBoolean();
    String optinalData = buf.readUtf();
    return new AgentActionPacket(entityId, cType, payload, optinalData);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;

      Entity e = player.level().getEntity(entityId);
      if (!(e instanceof AbstractAgentEntity agent)) return;
      switch (cType) {
        case AUTO_ARMOR   -> agent.setAutoArmor(payload);
        case FOLLOW       -> agent.setFollow(payload, optinalData);
        case AUTO_HOSTILE -> agent.setAutoHostile(payload);
        case DEBUG        -> agent.showTeam();
        default -> {}
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
