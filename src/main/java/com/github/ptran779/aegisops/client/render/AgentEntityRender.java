package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.client.AnimationHelper;
import com.github.ptran779.aegisops.client.animation.AnimationLibrary;
import com.github.ptran779.aegisops.client.model.AgentModel;
import com.github.ptran779.aegisops.entity.agent.*;
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
    // pose reset
    for (ModelPart part : model.BONE_PARTS.values()) {part.resetPose();}

    //attack ani
    float aniTime = (agent.tickCount - agent.renderTimeTrigger + partialTicks)/20f;
//    int aniMove = agent.getAniMove();

    // based animation
    if (agent.getAniMoveStateChange()){
      AnimationHelper.animateHumanoidToPose(model,
          AnimationLibrary.get(agent.getAniMovePoseStart()),
          agent.getAniMoveTimeStart(),
          AnimationLibrary.get(agent.getAniMovePoseEnd()),
          agent.getAniMoveTimeEnd(),
          aniTime, agent.getAniMoveTimeTran(),
          model.BONE_PARTS);
    } else {
      if (agent.getAniMovePoseStart() == AnimationLibrary.A_LIVING){
        // speed dictation
        double speed = agent.getDeltaMovement().horizontalDistanceSqr(); // X² + Z²
        if (speed > 0.015) {AnimationHelper.animateHumanoid(model,AnimationLibrary.get(AnimationLibrary.A_RUN), model.BONE_PARTS,aniTime,1,true);
        } else if (speed > 0.001) {
          AnimationHelper.animateHumanoid(model,AnimationLibrary.get(AnimationLibrary.A_WALK), model.BONE_PARTS,aniTime, 1, true);
        } else {
          AnimationHelper.animateHumanoid(model, AnimationLibrary.get(AnimationLibrary.A_IDLE), model.BONE_PARTS, aniTime, 1, true);
        }
        // gun holding
        if (agent.getMainHandItem().getItem() instanceof ModernKineticGunItem) {
          model.rightArm.yRot = model.head.yRot;
          model.leftArm.yRot = 0.5F + model.head.yRot;
          // tilt correction
          model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
          model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
          model.leftSleeve.copyFrom(model.leftArm);
          model.rightSleeve.copyFrom(model.rightArm);
        }
      } else {
        AnimationHelper.animateHumanoid(model,
            AnimationLibrary.get(agent.getAniMovePoseStart()),
            model.BONE_PARTS,
            aniTime,
            1,
            false
        );
      }
    }
//    } else if (aniMove == Utils.AniMove.SPECIAL) {
//      if (agent instanceof Engineer) {
//        if (agent.getAniMoveAdvance() == 0) {
//          animateHumanoid(model, AgentLivingAnimation.BONK, model.BONE_PARTS, aniTick / 20f, 1, false);
//        }
//      }
//      else if (agent instanceof Medic) {
//        switch (agent.getAniMoveAdvance()){
//          case 0: {
//            animateHumanoid(model, AgentLivingAnimation.MEDIC_BANDAGE, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//          case 1: {
//            animateHumanoid(model, AgentLivingAnimation.MEDIC_SYRINGE, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//        }
//      }
//      else if (agent instanceof Sniper) {
//        if (agent.getAniMoveAdvance() == 0) {
//          animateHumanoid(model, AgentLivingAnimation.PRECISION_SHOT, model.BONE_PARTS, aniTick / 20f, 1, false);
//        }
//      }
//      else if (agent instanceof Heavy) {
//        switch (agent.getAniMoveAdvance()) {
//          case 0: {
//            animateHumanoid(model, AgentSpecialAnimation.SHIELD_DEPLOY, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//          case 1: {
//            animateHumanoid(model, AgentSpecialAnimation.SHIELD_CHARGE, model.BONE_PARTS, aniTick / 20f, 1, true);
//            break;
//          }
//          case 2: {
//            animateHumanoid(model, AgentSpecialAnimation.SHIELD_HOLD, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//          case 3: {
//            animateHumanoid(model, AgentSpecialAnimation.SHIELD_BONK, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//          case 4: {
//            animateHumanoid(model, AgentSpecialAnimation.SHIELD_RECOVER, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//          case 5: {
//            animateHumanoid(model, AgentSpecialAnimation.SHIELD_GUN_BURST, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//          default: {
//            break;
//          }
//        }
//      }
//      else if (agent instanceof Soldier) {
//        if (agent.getAniMoveAdvance() == 0) {
//          animateHumanoid(model, AgentSpecialAnimation.SODIER_HYPE, model.BONE_PARTS, aniTick / 20f, 1, false);
//        }
//      }
//      else if (agent instanceof Demolition) {
//        switch (agent.getAniMoveAdvance()) {
//          case 0:{
//            animateHumanoid(model, AgentSpecialAnimation.GRENADE_THROW, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//          case 1: {
//            animateHumanoid(model, AgentSpecialAnimation.TERMINAL_ACTIVATE, model.BONE_PARTS, aniTick / 20f, 1, false);
//            break;
//          }
//        }
//      }
//    } else if (aniMove == Utils.AniMove.SALUTE) {
//      animateHumanoid(model, AgentLivingAnimation.SALUTE, model.BONE_PARTS, aniTick / 20f, 1, false);
//    } else if (aniMove == Utils.AniMove.DISP_RELOAD) {
//      animateHumanoid(model, AgentLivingAnimation.STATION_RELOAD, model.BONE_PARTS, aniTick / 20f, 1, false);
//    } else if (aniMove == Utils.AniMove.ATTACK) {
//      animateHumanoid(model, AgentLivingAnimation.TRIPLE_STRIKE, model.BONE_PARTS, aniTick / 20f, 1, false);
//    } else {  // normal living animation
//      double speed = agent.getDeltaMovement().horizontalDistanceSqr(); // X² + Z²
//      if (speed > 0.015) {
//        animateHumanoid(model, AgentLivingAnimation.RUN, model.BONE_PARTS, (agent.tickCount + partialTicks) / 20f, 1, true);
//      } else if (speed > 0.001) {
//        animateHumanoid(model, AgentLivingAnimation.WALK, model.BONE_PARTS, (agent.tickCount + partialTicks) / 20f, 1, true);
//      } else {
//        animateHumanoid(model,AgentLivingAnimation.IDLE, model.BONE_PARTS, (agent.tickCount + partialTicks) / 20f, 1, true);
//      }
//
//      //composite -- mostly what to do about hand
//      if (agent.getAniMove() == Utils.AniMove.RELOAD && aniTick < 20 * AgentLivingAnimation.RELOAD.lengthInSeconds()){
//        // strict impose
//        model.leftArm.resetPose();
//        model.rightArm.resetPose();
//        animateHumanoid(model, AgentLivingAnimation.RELOAD, model.BONE_PARTS, aniTick / 20f, 1, false);
//      } else if (agent.getMainHandItem().getItem() instanceof ModernKineticGunItem) {
//        model.rightArm.yRot = model.head.yRot;
//        model.leftArm.yRot = 0.5F + model.head.yRot;
//        // tilt correction
//        model.rightArm.xRot = (-(float)Math.PI / 2F) + model.head.xRot;
//        model.leftArm.xRot = (-(float)Math.PI / 2F) + model.head.xRot;
//
//        // Copy to sleeves
//        model.leftSleeve.copyFrom(model.leftArm);
//        model.rightSleeve.copyFrom(model.rightArm);
//      } else if (agent.isUsingItem() && (agent.getUseItem().getUseAnimation() == UseAnim.EAT || agent.getUseItem().getUseAnimation() == UseAnim.DRINK)) {
//        animateHumanoid(model, AgentLivingAnimation.EATING, model.BONE_PARTS, aniTick / 20f, 1, true);
//      }
//    }
    // sleeve fix

    // render head looking
    float headYaw = Mth.rotLerp(partialTicks, agent.yHeadRotO, agent.yHeadRot) - agent.yBodyRot;
    float headPitch = Mth.lerp(partialTicks, agent.xRotO, agent.getXRot());
    model.head.yRot += headYaw * (Mth.PI / 180F);
    model.head.xRot += headPitch * (Mth.PI / 180F);
    model.hat.copyFrom(model.head);

    super.render(agent, pEntityYaw, partialTicks, pPoseStack, pBuffer, pPackedLight);
  }
}



