package com.github.ptran779.aegisops.server;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.Config.AgentConfigManager;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeServerEvent {

  @SubscribeEvent
  public static void onConfigLoad(ServerStartingEvent event) {
    AgentConfigManager.serverGenerateDefault();
  }
}
