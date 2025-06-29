package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.Config.SkinManager;
import com.github.ptran779.aegisops.client.model.AgentModel;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class AgentEntityRender extends HumanoidMobRenderer<AbstractAgentEntity, AgentModel> {
    private final AgentModel standardModel;
    private final AgentModel slimModel;

    public AgentEntityRender(Context context) {
        super(context, new AgentModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.25f);
        this.standardModel = this.model;
        this.slimModel = new AgentModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);

        this.addLayer(new HumanoidArmorLayer<>(this,
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            context.getModelManager()
        ));
    }

    public ResourceLocation getTextureLocation(AbstractAgentEntity agent) {
        return agent.getResolvedSkin();
    }

    @Override
    public void render(AbstractAgentEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        this.model = pEntity.getFemale() ? slimModel : standardModel;
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}

