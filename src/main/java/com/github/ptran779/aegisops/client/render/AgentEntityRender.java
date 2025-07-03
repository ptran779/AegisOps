package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.client.animation.AgentAnimation;
import com.github.ptran779.aegisops.client.model.AgentModel;
import com.github.ptran779.aegisops.entity.Engineer;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.UseAnim;

import java.util.Objects;

import static com.github.ptran779.aegisops.client.AnimationHelper.animateHumanoid;

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
    public void render(AbstractAgentEntity agent, float pEntityYaw, float partialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        this.model = agent.getFemale() ? slimModel : standardModel;

        double speed = agent.getDeltaMovement().horizontalDistanceSqr(); // X² + Z²
        // pose reset
        for (ModelPart part : model.BONE_PARTS.values()) {
            part.resetPose();
        }

        //attack ani
        float aniTick = agent.tickCount - agent.timeTrigger + partialTicks;

        // based animation
        if (agent.getAniMove() == Utils.AniMove.SPECIAL) {
            if (agent instanceof Engineer && aniTick < 20 * AgentAnimation.BONK.lengthInSeconds()) {
                animateHumanoid(model, AgentAnimation.BONK, model.BONE_PARTS, aniTick / 20f, 1, false);
            }
        } else if (agent.getAniMove() == Utils.AniMove.DISP_RELOAD) {
            animateHumanoid(model, AgentAnimation.STATION_RELOAD, model.BONE_PARTS, aniTick / 20f, 1, false);
        } else {
            if (aniTick < 15 * AgentAnimation.STRIKE1.lengthInSeconds()) {
                animateHumanoid(model, AgentAnimation.STRIKE1, model.BONE_PARTS, aniTick / 15f, 1, false);
            } else if (speed > 0.015) {
                animateHumanoid(model, AgentAnimation.RUN, model.BONE_PARTS, (agent.tickCount + partialTicks) / 20f, 1, true);
            } else if (speed > 0.001) {
                animateHumanoid(model, AgentAnimation.WALK, model.BONE_PARTS, (agent.tickCount + partialTicks) / 20f, 1, true);
            } else {
                animateHumanoid(model, AgentAnimation.IDLE, model.BONE_PARTS, (agent.tickCount + partialTicks) / 20f, 1, true);
            }
        }
//        not sure what it for, so turn off tmp
//        float headYaw = Mth.rotLerp(partialTicks, agent.yHeadRotO, agent.yHeadRot) - agent.yBodyRot;
//        float headPitch = Mth.lerp(partialTicks, agent.xRotO, agent.getXRot());
//        model.head.yRot += headYaw * (Mth.PI / 180F);
//        model.head.xRot += headPitch * (Mth.PI / 180F);
//        model.hat.copyFrom(model.head);

        //composite -- mostly what to do about hand
        if (agent.getAniMove() == Utils.AniMove.RELOAD && aniTick < 20 * AgentAnimation.RELOAD.lengthInSeconds()){
            // strict impose
            model.leftArm.resetPose();
            model.rightArm.resetPose();
            animateHumanoid(model, AgentAnimation.RELOAD, model.BONE_PARTS, aniTick / 20f, 1, false);
        } else if (agent.getMainHandItem().getItem() instanceof ModernKineticGunItem) {
            model.rightArm.yRot = model.head.yRot;
            model.leftArm.yRot = 0.5F + model.head.yRot;
            // tilt correction
            model.rightArm.xRot = (-(float)Math.PI / 2F) + model.head.xRot;
            model.leftArm.xRot = (-(float)Math.PI / 2F) + model.head.xRot;

            // Copy to sleeves
            model.leftSleeve.copyFrom(model.leftArm);
            model.rightSleeve.copyFrom(model.rightArm);
        } else if (agent.isUsingItem() && agent.getUseItem().getUseAnimation() == UseAnim.EAT || agent.getUseItem().getUseAnimation() == UseAnim.DRINK) {
            //eating type
            int ticks = agent.getTicksUsingItem();
            // boby head
            model.head.xRot -= 0.20F * Mth.sin((float) ticks * 0.4f *Mth.PI);
            model.hat.xRot = model.head.xRot;
            //raise the arm
            model.leftArm.xRot = -Mth.HALF_PI; // = -1.5708F
            model.leftArm.yRot = 0.2F; // inward bend
            model.leftSleeve.copyFrom(model.leftArm);
        }

        super.render(agent, pEntityYaw, partialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}



