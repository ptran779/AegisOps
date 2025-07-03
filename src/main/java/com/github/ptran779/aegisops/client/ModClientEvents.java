package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.Config.SkinManager;
import com.github.ptran779.aegisops.client.model.*;
import com.github.ptran779.aegisops.client.render.*;
import com.github.ptran779.aegisops.server.BlockEntityInit;
import com.github.ptran779.aegisops.server.EntityInit;
import com.github.ptran779.aegisops.server.MenuInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public final class ModClientEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(DropPodModel.LAYER_LOCATION, DropPodModel::createBodyLayer);
        event.registerLayerDefinition(BeaconModel.LAYER_LOCATION, BeaconModel::createBodyLayer);
        event.registerLayerDefinition(DBTurretModel.LAYER_LOCATION, DBTurretModel::createBodyLayer);
        event.registerLayerDefinition(DBTurretReadyModel.LAYER_LOCATION, DBTurretReadyModel::createBodyLayer);
        event.registerLayerDefinition(TurretBulletModel.LAYER_LOCATION, TurretBulletModel::createBodyLayer);
        event.registerLayerDefinition(PortDispModel.LAYER_LOCATION, PortDispModel::createBodyLayer);
    }
    //Entity Render
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.SOLDIER.get(), AgentEntityRender::new);
        event.registerEntityRenderer(EntityInit.SNIPER.get(), AgentEntityRender::new);
        event.registerEntityRenderer(EntityInit.HEAVY.get(), AgentEntityRender::new);
        event.registerEntityRenderer(EntityInit.DEMOLITION.get(), AgentEntityRender::new);
        event.registerEntityRenderer(EntityInit.MEDIC.get(), AgentEntityRender::new);
        event.registerEntityRenderer(EntityInit.ENGINEER.get(), AgentEntityRender::new);
        event.registerEntityRenderer(EntityInit.SWORDMAN.get(), AgentEntityRender::new);

        event.registerBlockEntityRenderer(BlockEntityInit.BEACON_BE.get(), BeaconBERender::new) ;
        event.registerBlockEntityRenderer(BlockEntityInit.DROP_POD_BE.get(), DropPodBERender::new) ;
        event.registerEntityRenderer(EntityInit.FALLING_DROP_POD.get(), DropPodFallingRender::new);
        event.registerEntityRenderer(EntityInit.BD_TURRET.get(), DBTurretRender::new);
        event.registerEntityRenderer(EntityInit.TURRET_BULLET.get(), TurretBulletRender::new);
        event.registerEntityRenderer(EntityInit.PORT_DISP.get(), PortDispRender::new);

    }
    //Screen
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MenuInit.AEGISOPS_MENU.get(), AgentInventoryScreen::new);
            //custom skin manager
            SkinManager.init();
        });
    }
}
