package com.github.ptran779.aegisops.network.ml_packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;


//S->C
public class BrainChipScreen {
  private final UUID modelUUID;
  private final int inputLen;
  private final int outputLen;
  private final boolean trainMode;
  private final byte[] modelByte;
  private final byte[] configByte;

  public BrainChipScreen(UUID modelUUID, int inputLen, int outputLen, boolean trainMode, byte[] configByte, byte[] modelByte) {
    this.modelUUID = modelUUID;
    this.inputLen = inputLen;
    this.outputLen = outputLen;
    this.trainMode = trainMode;
    this.configByte = configByte;
    this.modelByte = modelByte;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(modelUUID);
    buf.writeVarInt(inputLen);
    buf.writeVarInt(outputLen);
    buf.writeBoolean(trainMode);
    buf.writeBytes(configByte);
    buf.writeByteArray(modelByte);
    // safety
    if (configByte.length != 48) {
      throw new RuntimeException("Config must be 48 bytes!");
    }
  }

  public static BrainChipScreen decode(FriendlyByteBuf buf) {
    UUID uuid = buf.readUUID();
    int input = buf.readVarInt();
    int output = buf.readVarInt();
    boolean trainMode = buf.readBoolean();
    // 1. Read Config (Fixed 48 bytes)
    byte[] config = new byte[48];
    buf.readBytes(config); // Netty reads 48 bytes into the array
    // 2. Read Model (Variable length)
    byte[] model = buf.readByteArray();
    return new BrainChipScreen(uuid, input, output, trainMode, config, model);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.openScreen(modelUUID, inputLen, outputLen, trainMode, configByte, modelByte));});
    ctx.get().setPacketHandled(true);
  }

  // This inner class is only loaded if DistExecutor calls it
  // It keeps the imports isolated.
  private static class ClientHandler {
    public static void openScreen(UUID uuid, int inputLen, int outputLen, boolean trainMode, byte[] rawConfig, byte[] rawDat) {
      // It is safe to import/use client classes here
      com.github.ptran779.aegisops.client.BrainChipScreen screen = new com.github.ptran779.aegisops.client.BrainChipScreen(uuid, inputLen, outputLen, trainMode);
      // wip load canvas layer from json
      screen.setCurModel(rawDat);
      screen.setTrainConfig(rawConfig);
      // load screen
      net.minecraft.client.Minecraft.getInstance().setScreen(screen);
    }
  }
}