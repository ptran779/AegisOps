package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.entity.FallingDropPod;
import com.github.ptran779.aegisops.entity.Heavy;
import com.github.ptran779.aegisops.entity.Sniper;
import com.github.ptran779.aegisops.entity.Soldier;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModServerEvent {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityInit.SOLDIER.get(), Soldier.createAttributes().build());
        event.put(EntityInit.SNIPER.get(), Sniper.createAttributes().build());
        event.put(EntityInit.HEAVY.get(), Heavy.createAttributes().build());
        event.put(EntityInit.DEMOLITION.get(), Heavy.createAttributes().build());
        event.put(EntityInit.MEDIC.get(), Heavy.createAttributes().build());
        event.put(EntityInit.ENGINEER.get(), Heavy.createAttributes().build());
        event.put(EntityInit.SWORDMAN.get(), Heavy.createAttributes().build());
        event.put(EntityInit.FALLING_DROP_POD.get(), FallingDropPod.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerPacketHandler(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }
}
