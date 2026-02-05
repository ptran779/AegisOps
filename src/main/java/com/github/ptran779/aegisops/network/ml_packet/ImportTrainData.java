package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.config.MlModelManager;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

// C -> S
public class ImportTrainData {
  String filename;
  UUID uuid;

  public ImportTrainData(UUID uuid, String filename) {
    this.uuid = uuid;
    this.filename = filename;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(uuid);
    buf.writeUtf(filename);
  }

  public static ImportTrainData decode(FriendlyByteBuf buf) {
    return new ImportTrainData(buf.readUUID(), buf.readUtf());
  }

  public void handle(Supplier<NetworkEvent.Context> ctx){
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) {return;}
      MlModelManager.MLUnit unit = MlModelManager.getMUnit(uuid, player.level().getGameTime());
      if (unit.dataManager == null) {
        System.out.println("[AegisOps Critical] This should never happen Please check all critical infrastructure!");
        return;
      }
      // FIXME send to train thread --- maybe
      String msg = MlModelManager.importData(unit, filename);
      // send to client UpdateDatSizePacket
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player), new PushDatLog(msg));
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),
          new UpdateTrainDataSize(unit.dataManager.rawDat.size()));
    });
    ctx.get().setPacketHandled(true);
  }
}
