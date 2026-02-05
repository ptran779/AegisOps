package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.config.MlModelManager;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

//C->S
public class ClearTrainData {
  UUID unitUUID;
  public ClearTrainData(UUID unitUUID) {
    this.unitUUID = unitUUID;
  }
  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(unitUUID);
  }
  public static ClearTrainData decode(FriendlyByteBuf buf) {
    return new ClearTrainData(buf.readUUID());
  }
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) {
        return;
      }
      MlModelManager.MLUnit unit = MlModelManager.getMUnit(unitUUID, player.level().getGameTime());
      if (unit.dataManager == null) {return;}  // nothing to clear
      unit.dataManager.rawDat.clear();
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new UpdateTrainDataSize(0));
    });
    ctx.get().setPacketHandled(true);
  }
}
