package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.client.BrainChipScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//S->C
public class PushDatLog {
  String log;
  public PushDatLog(String log) {
    this.log = log;
  }
  public void encode(FriendlyByteBuf buf) {
    buf.writeUtf(log);
  }
  public static PushDatLog decode(FriendlyByteBuf buf) {
    return new PushDatLog(buf.readUtf());
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> ClientHandler.handlePacket(log));
    ctx.get().setPacketHandled(true);
  }
  private static class ClientHandler {
    public static void handlePacket(String str) {
      // Check if the player is actually looking at the screen
      if (net.minecraft.client.Minecraft.getInstance().screen instanceof BrainChipScreen screen) {
        screen.addLog(str);
      }
    }
  }
}
