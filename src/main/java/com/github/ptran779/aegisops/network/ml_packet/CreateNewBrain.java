package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.brain.ml.ML;
import com.github.ptran779.aegisops.config.MlModelManager;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

// C -> S
public class CreateNewBrain {
  UUID modelUUID;
  byte[] data;

  public CreateNewBrain(UUID modelUUID, byte[] data) {
    this.modelUUID = modelUUID;
    this.data = data;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(modelUUID);
    buf.writeByteArray(data);
  }

  public static CreateNewBrain decode(FriendlyByteBuf buf) {
    return new CreateNewBrain(buf.readUUID(), buf.readByteArray());
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) {
        return;
      }
      MlModelManager.MLUnit mlunit = MlModelManager.getMUnit(modelUUID, player.level().getGameTime());
      if (mlunit.model == null) {
        PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new PushDatLog("Model Initialized"));
      } else {
        PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player), new PushDatLog("Model Overwritten"));
      }
      mlunit.model = ML.createModelFromSerialization(data, true);
    });
    ctx.get().setPacketHandled(true);
  }
}
