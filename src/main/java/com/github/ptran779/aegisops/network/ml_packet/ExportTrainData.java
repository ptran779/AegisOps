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
public class ExportTrainData {
  UUID unitUUID;
  String filename;
  public ExportTrainData(UUID modelUUID, String filename) {
    this.unitUUID = modelUUID;
    this.filename = filename;
  }
  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(unitUUID);
    buf.writeUtf(filename);
  }
  public static ExportTrainData decode(FriendlyByteBuf buf) {
    return new ExportTrainData(buf.readUUID(), buf.readUtf());
  }
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) {return;}
      MlModelManager.MLUnit unit = MlModelManager.getMUnit(unitUUID, player.level().getGameTime());
      if (unit.dataManager == null || unit.dataManager.rawDat.isEmpty()) {return;}
      String msg = MlModelManager.exportData(unit, filename);
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player), new PushDatLog(msg));
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),
          new GetTrainDataList(MlModelManager.getAvailableCsvs()));
    });
    ctx.get().setPacketHandled(true);
  }
}
