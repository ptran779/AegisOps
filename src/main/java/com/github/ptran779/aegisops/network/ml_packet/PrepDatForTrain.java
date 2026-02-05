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
public class PrepDatForTrain {
  private final UUID unitUUID;

  public PrepDatForTrain(UUID unitId) {
    this.unitUUID = unitId;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(unitUUID);
  }

  public static PrepDatForTrain decode(FriendlyByteBuf buf) {
    return new PrepDatForTrain(buf.readUUID());
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) return;
      MlModelManager.MLUnit unit = MlModelManager.getMUnit(unitUUID, player.level().getGameTime());
      if (unit == null || unit.model == null || unit.dataManager == null) {
        PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new PushDatLog("There's no data manager"));
      } else if (unit.dataManager.rawDat.isEmpty()) {
        PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new PushDatLog("Empty Data"));
      } else {
        unit.dataManager.prepareData(unit.model.valFrac,unit.model.testFrac);
        PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new PushDatLog("Model Is ready for training"));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}