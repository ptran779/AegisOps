package com.github.ptran779.aegisops.network.Agent;

import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AgentSpecialPacket {
  private final int entityId;
  private final boolean payload;          //action payload -- expanse me if need more complex data communication

  public AgentSpecialPacket(int entityId, boolean flag){
    this.entityId = entityId;
    this.payload = flag;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeBoolean(payload);
  }

  public static AgentSpecialPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    boolean payload = buf.readBoolean();
    return new AgentSpecialPacket(entityId, payload);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;

      Entity e = player.level().getEntity(entityId);
      if (!(e instanceof AbstractAgentEntity agent)) return;
      agent.setAllowSpecial(payload);
    });
    ctx.get().setPacketHandled(true);
  }
}
//agent.setAllowSpecial(payload)