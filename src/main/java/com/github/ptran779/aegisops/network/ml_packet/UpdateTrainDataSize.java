package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.client.BrainChipScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//S->C
public class UpdateTrainDataSize {
  private final int size;
  public UpdateTrainDataSize(int size) {
    this.size = size;
  }
  public void encode(FriendlyByteBuf buf) {
    buf.writeVarInt(size);
  }
  public static UpdateTrainDataSize decode(FriendlyByteBuf buf) {
    return new UpdateTrainDataSize(buf.readVarInt());
  }
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> UpdateTrainDataSize.ClientHandler.handlePacket(size));
    });
    ctx.get().setPacketHandled(true);
  }
  private static class ClientHandler {
    public static void handlePacket(int size) {
      // Check if the player is actually looking at the screen
      if (net.minecraft.client.Minecraft.getInstance().screen instanceof BrainChipScreen screen) {
        screen.updateDatSize(size);
      }
    }
  }
}
