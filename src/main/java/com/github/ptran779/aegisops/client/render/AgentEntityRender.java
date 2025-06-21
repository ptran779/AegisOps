package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.client.model.AgentModel;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class AgentEntityRender extends HumanoidMobRenderer<AbstractAgentEntity, AgentModel> {
    public AgentEntityRender(Context context) {
        //based entity
        super(context, new AgentModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.25f);
        //armor layer
        this.addLayer(new HumanoidArmorLayer<>(this,new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractAgentEntity agent) {
        return agent.getTexture();
    }
}

