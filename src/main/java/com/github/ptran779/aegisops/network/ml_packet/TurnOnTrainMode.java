package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.brain.ml.DataManager;
import com.github.ptran779.aegisops.config.MlModelManager;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

//C->S
public class TurnOnTrainMode {
  UUID unitUUID;
  boolean mode;

  public TurnOnTrainMode(UUID unitUUID, boolean mode) {
    this.unitUUID = unitUUID;
    this.mode = mode;
  }
  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(unitUUID);
    buf.writeBoolean(mode);
  }
  public static TurnOnTrainMode decode(FriendlyByteBuf buf) {
    return new TurnOnTrainMode(buf.readUUID(), buf.readBoolean());
  }
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) {
        return;
      }
      MlModelManager.MLUnit mlUnit = MlModelManager.getMUnit(unitUUID, player.level().getGameTime());
      if (mode) {
        mlUnit.dataManager = new DataManager();
      } else {
        mlUnit.dataManager = null;
      }
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new UpdateTrainDataSize(0));
    });
    ctx.get().setPacketHandled(true);
  }
}
