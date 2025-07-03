package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.AnimationHelper;
import com.github.ptran779.aegisops.client.animation.PortDispAnimation;
import com.github.ptran779.aegisops.client.model.PortDispModel;
import com.github.ptran779.aegisops.entity.PortDisp;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class PortDispRender extends EntityRenderer<PortDisp> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/entities/portdisp.png");
  private final PortDispModel model;
//  private final DBTurretReadyModel modelReady;

  public PortDispRender(EntityRendererProvider.Context pContext) {
    super(pContext);
    this.model = new PortDispModel(pContext.bakeLayer(PortDispModel.LAYER_LOCATION));
//    this.modelReady = new DBTurretReadyModel(pContext.bakeLayer(DBTurretReadyModel.LAYER_LOCATION));
  }

  public void render(PortDisp disp, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
    pPoseStack.pushPose();
    // Positioning the model at the entity’s current coordinates
    pPoseStack.translate(0.0D, 1.5D, 0.0D); // Adjust Y to match model origin
    pPoseStack.scale(-1F, -1F, 1F); // Flip model
    pPoseStack.mulPose(Axis.YP.rotationDegrees(180));

    float aniTick = disp.tickCount - disp.timeTrigger + pPartialTick;
    if (disp.getOpen()){
      AnimationHelper.animate(model, PortDispAnimation.DEPLOY, aniTick / 20f, 1);
    } else if (aniTick < PortDispAnimation.DEPLOY.lengthInSeconds() * 20) {
      AnimationHelper.animate(model, PortDispAnimation.DEPLOY, PortDispAnimation.DEPLOY.lengthInSeconds() - aniTick  / 20f, 1);
    } else {
      model.getRoot().getAllParts().forEach(ModelPart::resetPose);  // clean animation
    }

    VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(TEXTURE));
    model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    pPoseStack.popPose();
  }

  public ResourceLocation getTextureLocation(PortDisp disp) {return TEXTURE;}
}
