package com.github.ptran779.aegisops.network;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AgentBoolPacket {
  private final int entityId;
  private final AgentCommandType cType;
  private final boolean payload;          //action payload -- expanse me if need more complex data communication

  public AgentBoolPacket(int entityId, AgentCommandType cType, boolean flag){
    this.entityId = entityId;
    this.cType = cType;
    this.payload = flag;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeEnum(cType);
    buf.writeBoolean(payload);
  }

  public static AgentBoolPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    AgentCommandType cType = buf.readEnum(AgentCommandType.class);
    boolean payload = buf.readBoolean();
//    UUID optinalData = buf.readUUID();
    return new AgentBoolPacket(entityId, cType, payload);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;

      Entity e = player.level().getEntity(entityId);
      if (!(e instanceof AbstractAgentEntity agent)) return;
      switch (cType) {
        case AUTO_ARMOR   -> agent.setAutoArmor(payload);
        case ATTACK_PLAYER -> agent.setAttackPlayer(payload);
        case REMOVE -> {
          agent.setOwnerUUID(null);
          agent.updateBossInfo();
        }
        default -> {}
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
