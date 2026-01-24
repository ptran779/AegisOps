package com.github.ptran779.aegisops.network.player;

import com.github.ptran779.aegisops.config.AgentConfigManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class serverConfigPacket {

  private final String jsonPayload;

  // Constructor for creating the packet on the Server
  public serverConfigPacket(String jsonPayload) {
    this.jsonPayload = jsonPayload;
  }

  // Encoder: Writes the string to the network buffer
  public void encode(FriendlyByteBuf buf) {
    // We allow up to 1MB (1048576 bytes) to prevent "String too long" errors
    buf.writeUtf(this.jsonPayload, 1048576);
  }

  // Decoder: Reads the string from the network buffer
  public static serverConfigPacket decode(FriendlyByteBuf buf) {
    // Read with the same 1MB limit
    String payload = buf.readUtf(1048576);
    return new serverConfigPacket(payload);
  }

  // Handler: Runs on the Client when the packet arrives
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      // Hand off the data to your Config Manager
      // This runs safely on the Client Main Thread
      AgentConfigManager.handleSync(this.jsonPayload);
    });
    ctx.get().setPacketHandled(true);
  }
}