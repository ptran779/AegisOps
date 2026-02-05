package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.config.SkinManager;
import com.github.ptran779.aegisops.client.model.*;
import com.github.ptran779.aegisops.client.render.*;
import com.github.ptran779.aegisops.server.BlockEntityInit;
import com.github.ptran779.aegisops.server.EntityInit;
import com.github.ptran779.aegisops.server.MenuInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public final class ModClientEvents {
    public static ModularShieldRender MODULAR_SHIELD_RENDER_INSTANCE;
    public static GrenadeItemRender GRENADE_RENDER_INSTANCE;

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(DropPodModel.LAYER_LOCATION, DropPodModel::createBodyLayer);
        event.registerLayerDefinition(HellpodModel.LAYER_LOCATION, HellpodModel::createBodyLayer);
        event.registerLayerDefinition(BeaconModel.LAYER_LOCATION, BeaconModel::createBodyLayer);

        event.registerLayerDefinition(PortDispModel.LAYER_LOCATION, PortDispModel::createBodyLayer);
        event.registerLayerDefinition(DBTurretModel.LAYER_LOCATION, DBTurretModel::createBodyLayer);
        event.registerLayerDefinition(DBTurretReadyModel.LAYER_LOCATION, DBTurretReadyModel::createBodyLayer);
        event.registerLayerDefinition(VectorPursuerModel.LAYER_LOCATION, VectorPursuerModel::createBodyLayer);

        event.registerLayerDefinition(TurretBulletModel.LAYER_LOCATION, TurretBulletModel::createBodyLayer);

        event.registerLayerDefinition(ModularShieldModel.LAYER_LOCATION, ModularShieldModel::createBodyLayer);
        event.registerLayerDefinition(GrenadeModel.LAYER_LOCATION, GrenadeModel::createBodyLayer);
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
        event.registerBlockEntityRenderer(BlockEntityInit.DROP_POD_BE.get(), DropPodBERender::new);
        event.registerEntityRenderer(EntityInit.FALLING_DROP_POD.get(), FallingDropPodRender::new);
        event.registerBlockEntityRenderer(BlockEntityInit.HELL_POD_BE.get(), HellPodBERender::new);
        event.registerEntityRenderer(EntityInit.FALLING_HELL_POD.get(), FallingHellPodRender::new);
        event.registerEntityRenderer(EntityInit.BD_TURRET.get(), DBTurretRender::new);
        event.registerEntityRenderer(EntityInit.TURRET_BULLET.get(), TurretBulletRender::new);
        event.registerEntityRenderer(EntityInit.PORT_DISP.get(), PortDispRender::new);
        event.registerEntityRenderer(EntityInit.GRENADE.get(), GrenadeEntityRender::new);

        event.registerEntityRenderer(EntityInit.VECTOR_PURSUER.get(), VectorPursuerRender::new);
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        EntityModelSet modelSet = event.getEntityModels();
        BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        MODULAR_SHIELD_RENDER_INSTANCE = new ModularShieldRender(dispatcher, modelSet);
        GRENADE_RENDER_INSTANCE = new GrenadeItemRender(dispatcher, modelSet);
    }

    //Screen
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // menu
            MenuScreens.register(MenuInit.AEGISOPS_MENU.get(), AgentInventoryScreen::new);
            //custom skin manager
            SkinManager.init();
        });
    }
}
