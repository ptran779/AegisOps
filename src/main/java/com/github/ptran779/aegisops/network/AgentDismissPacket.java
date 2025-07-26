package com.github.ptran779.aegisops.network;

import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AgentDismissPacket {
  private final int entityId;
  private final boolean payload;          //action payload -- expanse me if need more complex data communication

  public AgentDismissPacket(int entityId, boolean flag){
    this.entityId = entityId;
    this.payload = flag;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeBoolean(payload);
  }

  public static AgentDismissPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    boolean payload = buf.readBoolean();
    return new AgentDismissPacket(entityId, payload);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;

      Entity e = player.level().getEntity(entityId);
      if (!(e instanceof AbstractAgentEntity agent)) return;
      agent.setBossUUID(null);
    });
    ctx.get().setPacketHandled(true);
  }
}
//agent.setAllowSpecial(payload)