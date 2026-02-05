package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.config.MlModelManager;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateBrainConfig {
    UUID modelUUID;
    byte[] data;

    public UpdateBrainConfig(UUID modelUUID, byte[] data) {
        this.modelUUID = modelUUID;
        this.data = data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(modelUUID);
        buf.writeByteArray(data);
    }

    public static UpdateBrainConfig decode(FriendlyByteBuf buf) {
        return new UpdateBrainConfig(buf.readUUID(), buf.readByteArray());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            MlModelManager.MLUnit mlunit = MlModelManager.getMUnit(modelUUID, player.level().getGameTime());
            if (mlunit.model == null) {
                PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),
                    new PushDatLog("Model is empty. Can't push config"));
            } else {
                mlunit.model.trainConfigDeserialize(data);
                PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> player),
                    new PushDatLog("Model config updated"));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
