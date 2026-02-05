package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.config.MlModelManager;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

//C -> S
public class CommitTrainModel {
  boolean commit;
  UUID unitUUID;

  public CommitTrainModel(boolean commit, UUID unitUUID) {
    this.commit = commit;
    this.unitUUID = unitUUID;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeBoolean(commit);
    buf.writeUUID(unitUUID);
  }

  public static CommitTrainModel decode(FriendlyByteBuf buf) {
    return new CommitTrainModel(buf.readBoolean(), buf.readUUID());
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    NetworkEvent.Context context = ctx.get();
    context.enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      long gameTime = player.level().getGameTime();
      MlModelManager.MLUnit unit = MlModelManager.getMUnit(unitUUID, gameTime);

      if (commit) {
        unit.model = unit.model2;
        unit.model2 = null;
        PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new PushDatLog("Model Commited"));
      } else {
        unit.model2 = null; // purge for GC
        PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new PushDatLog("Model restore"));
      }
    });
    context.setPacketHandled(true);
  }
}
