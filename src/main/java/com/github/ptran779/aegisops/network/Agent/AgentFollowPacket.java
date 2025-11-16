package com.github.ptran779.aegisops.network.Agent;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AgentFollowPacket {
  private final int entityId;
  private final int payload;          //action payload -- expanse me if need more complex data communication
  private final UUID targetUUID;

  public AgentFollowPacket(int entityId, int flag, UUID targetUUID){
    this.entityId = entityId;
    this.payload = flag;
    this.targetUUID = targetUUID;
  }

  public AgentFollowPacket(int entityId, int flag){
    this(entityId, flag, null);
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeInt(payload);
    buf.writeUUID(targetUUID);
  }

  public static AgentFollowPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    int payload = buf.readInt();
    UUID optinalData = buf.readUUID();
    return new AgentFollowPacket(entityId, payload, optinalData);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;
//      Entity e = player.level().getEntity(entityId);
      if (player.level().getEntity(entityId) instanceof AbstractAgentEntity agent) {
        agent.setFollowMode(Utils.FollowMode.values()[payload], targetUUID);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
