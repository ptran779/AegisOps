package com.github.ptran779.aegisops.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CameraModePacket {
  // No data needed, so empty constructor
  public CameraModePacket() {}

  public static void encode(CameraModePacket msg, FriendlyByteBuf buf) {}
  public static CameraModePacket decode(FriendlyByteBuf buf) {
    return new CameraModePacket();
  }

  public static void handle(CameraModePacket msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
    });
    ctx.get().setPacketHandled(true);
  }
}
