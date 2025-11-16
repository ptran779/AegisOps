package com.github.ptran779.aegisops.network.Agent;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AgentHostilePacket {
  private final int entityId;
  private final int payload;          //action payload -- expanse me if need more complex data communication

  public AgentHostilePacket(int entityId, int flag){
    this.entityId = entityId;
    this.payload = flag;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeInt(payload);
  }

  public static AgentHostilePacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    int payload = buf.readInt();
    return new AgentHostilePacket(entityId, payload);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;
      if (player.level().getEntity(entityId) instanceof AbstractAgentEntity agent) {
        agent.setTargetMode(Utils.TargetMode.values()[payload]);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
