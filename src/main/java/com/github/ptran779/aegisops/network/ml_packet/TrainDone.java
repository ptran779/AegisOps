package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.client.BrainChipScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class TrainDone {
  public TrainDone() {}

  public void encode(FriendlyByteBuf buf) {}
  public static TrainDone decode(FriendlyByteBuf buf) {return new TrainDone();}

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientHandler::handle);});
    ctx.get().setPacketHandled(true);
  }

  private static class ClientHandler {
    public static void handle() {
      if (net.minecraft.client.Minecraft.getInstance().screen instanceof BrainChipScreen screen) {
        screen.trainDone(true);
      }
    }
  }
}