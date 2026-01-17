package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.item.brain.IChipBrain;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilitiesInit {
  public static Capability<IChipBrain> CAP_CHIP_BRAIN = null;

  @SubscribeEvent
  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.register(IChipBrain.class);
  }
}
