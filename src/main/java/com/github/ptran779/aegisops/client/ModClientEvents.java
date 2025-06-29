package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.Config.SkinManager;
import com.github.ptran779.aegisops.client.model.BeaconModel;
import com.github.ptran779.aegisops.client.model.DropPodModel;
import com.github.ptran779.aegisops.client.render.AgentEntityRender;
import com.github.ptran779.aegisops.client.render.BeaconBERender;
import com.github.ptran779.aegisops.client.render.DropPodBERender;
import com.github.ptran779.aegisops.client.render.DropPodFallingRender;
import com.github.ptran779.aegisops.server.BlockEntityInit;
import com.github.ptran779.aegisops.server.EntityInit;
import com.github.ptran779.aegisops.server.MenuInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AegisOps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public final class ModClientEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(DropPodModel.LAYER_LOCATION, DropPodModel::createBodyLayer);
        event.registerLayerDefinition(BeaconModel.LAYER_LOCATION, BeaconModel::createBodyLayer);
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
