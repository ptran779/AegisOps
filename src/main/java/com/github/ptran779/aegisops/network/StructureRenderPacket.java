package com.github.ptran779.aegisops.network;

import com.github.ptran779.aegisops.entity.DBTurret;
import com.github.ptran779.aegisops.entity.PortDisp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// test for swing for now
public class StructureRenderPacket {
  private final int entityId;
  private final float payload;          //action payload -- expanse me if need more complex data communication

  public StructureRenderPacket(int entityId, float payload){
    this.entityId = entityId;
    this.payload = payload;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(entityId);
    buf.writeFloat(payload);
  }

  public static StructureRenderPacket decode(FriendlyByteBuf buf){
    int entityId = buf.readInt();
    int payload = buf.readInt();
    return new StructureRenderPacket(entityId, payload);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientLevel level = Minecraft.getInstance().level;
      if (level == null) return;

      Entity entity = level.getEntity(entityId);
      if ((entity instanceof DBTurret turret)) turret.cannonProgress = payload;  // reset cannon progress
      else if (entity instanceof PortDisp disp) {disp.timeTrigger = disp.tickCount;}
    });
    ctx.get().setPacketHandled(true);
  }
}
