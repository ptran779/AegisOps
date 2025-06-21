package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.entity.Heavy;
import com.github.ptran779.aegisops.entity.Sniper;
import com.github.ptran779.aegisops.entity.Soldier;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModServerEvent {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityInit.SOLDIER.get(), Soldier.createAttributes().build());
        event.put(EntityInit.SNIPER.get(), Sniper.createAttributes().build());
        event.put(EntityInit.HEAVY.get(), Heavy.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerPacketHandler(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }
}
