package com.github.ptran779.aegisops.network.Agent;

import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeSkinPacket {
  private final int entityId;
  private final String payload;          //action payload -- expanse me if need more complex data communication

  public ChangeSkinPacket(int entityId, String payload){
    this.entityId = entityId;
    this.payload = payload;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeUtf(payload);
  }

  public static ChangeSkinPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    String payload = buf.readUtf();
    return new ChangeSkinPacket(entityId, payload);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;

      Entity e = player.level().getEntity(entityId);
      if (!(e instanceof AbstractAgentEntity agent)) return;
      agent.setSkin(payload);
    });
    ctx.get().setPacketHandled(true);
  }
}
