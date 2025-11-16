package com.github.ptran779.aegisops.network.render;

import com.github.ptran779.aegisops.entity.api.IEntityRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// test for swing for now
public class EntityRenderPacket {
  private final int entityId;
  private final int payload;          //action payload -- not sure why I need this...

  public EntityRenderPacket(int entityId, int payload){
    this.entityId = entityId;
    this.payload = payload;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeInt(payload);
  }

  public static EntityRenderPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    int payload = buf.readInt();
    return new EntityRenderPacket(entityId, payload);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientLevel level = Minecraft.getInstance().level;
      if (level == null) return;

      Entity entity = level.getEntity(entityId);
      if (!(entity instanceof IEntityRender iEntity)) return;
      iEntity.resetRenderTick();
//      agent.timeTrigger = agent.tickCount;  // reset swing progress
    });
    ctx.get().setPacketHandled(true);
  }
}
