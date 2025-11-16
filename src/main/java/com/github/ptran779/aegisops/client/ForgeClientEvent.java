package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.github.ptran779.aegisops.network.player.KeyBindPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvent {
  @SubscribeEvent
  public static void onKeyInput(InputEvent.Key event) {
    if (KeyBinding.FOLLOW_KEY.consumeClick()) {
      PacketHandler.CHANNELS.sendToServer(new KeyBindPacket(0));
    } else if (KeyBinding.TARGET_KEY.consumeClick()) {
      PacketHandler.CHANNELS.sendToServer(new KeyBindPacket(1));
    } else if (KeyBinding.SPECIAL_KEY.consumeClick()) {
      PacketHandler.CHANNELS.sendToServer(new KeyBindPacket(2));
    }
  }
}
