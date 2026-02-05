package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.brain.api.BrainServer;
import com.github.ptran779.aegisops.config.MlModelManager;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

import static com.github.ptran779.aegisops.server.ForgeServerEvent.BRAIN_SERVER;

//C -> S
public class TrainBrainChip {
  UUID unitUUID;
  public TrainBrainChip(UUID unitUUID) {
    this.unitUUID = unitUUID;
  }
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeUUID(unitUUID);
  }
  public static TrainBrainChip decode(FriendlyByteBuf buf) {
    return new TrainBrainChip(buf.readUUID());
  }
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayer player = ctx.get().getSender();
      if (player == null) {return;}
      MlModelManager.MLUnit unit = MlModelManager.getMUnit(unitUUID, player.level().getGameTime());
      // fixme need to handle max
      if (unit == null || unit.model == null || unit.dataManager == null) {
        PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new PushDatLog("Missing Stuff Cant train"));
        return;
      }
      BRAIN_SERVER.TASK_QUEUE_TRAIN.add(new BrainServer.TrainDatIn(player.getUUID(), BrainServer.TARGET_RECEIVER.PLAYER, unitUUID, unit.model, unit.dataManager));
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),new PushDatLog("Training Sent To Server"));
    });
    ctx.get().setPacketHandled(true);
  }
}
