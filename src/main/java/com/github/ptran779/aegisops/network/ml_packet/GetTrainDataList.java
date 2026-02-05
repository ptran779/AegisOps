package com.github.ptran779.aegisops.network.ml_packet;

import com.github.ptran779.aegisops.client.BrainChipScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//S->C
public class GetTrainDataList {
  private final List<String> fileNames;

  public GetTrainDataList(List<String> fileNames) {
    this.fileNames = fileNames;
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(fileNames.size());
    for (String s : fileNames) {
      buf.writeUtf(s);
    }
  }

  public static GetTrainDataList decode(FriendlyByteBuf buf) {
    int size = buf.readInt();
    List<String> list = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      list.add(buf.readUtf());
    }
    return new GetTrainDataList(list);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->ClientHandler.handlePacket(fileNames));
    });
    ctx.get().setPacketHandled(true);
  }

  private static class ClientHandler {
    public static void handlePacket(List<String> files) {
      // Check if the player is actually looking at the screen
      if (net.minecraft.client.Minecraft.getInstance().screen instanceof BrainChipScreen screen) {
        // Update your DropDownWidget here
        screen.updateImportList(files);
      }
    }
  }
}