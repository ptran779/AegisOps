package com.github.ptran779.aegisops.network;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// test for swing for now
public class AgentRenderPacket {
  private final int entityId;
  private final int payload;          //action payload -- not sure why I need this...

  public AgentRenderPacket(int entityId, int payload){
    this.entityId = entityId;
    this.payload = payload;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeInt(payload);
  }

  public static AgentRenderPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    int payload = buf.readInt();
    return new AgentRenderPacket(entityId, payload);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientLevel level = Minecraft.getInstance().level;
      if (level == null) return;

      Entity entity = level.getEntity(entityId);
      if (!(entity instanceof AbstractAgentEntity agent)) return;
      agent.timeTrigger = agent.tickCount;  // reset swing progress
    });
    ctx.get().setPacketHandled(true);
  }
}
