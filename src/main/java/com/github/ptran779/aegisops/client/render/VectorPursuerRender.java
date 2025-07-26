package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.AnimationHelper;
import com.github.ptran779.aegisops.client.animation.DBTurretAnimation;
import com.github.ptran779.aegisops.client.animation.VectorPursuerAnimation;
import com.github.ptran779.aegisops.client.model.VectorPursuerModel;
import com.github.ptran779.aegisops.entity.extra.VectorPursuer;
import com.github.ptran779.aegisops.entity.structure.DBTurret;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VectorPursuerRender extends EntityRenderer<VectorPursuer> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/entities/vector_pursuer.png");
  private final VectorPursuerModel model;

  public VectorPursuerRender(EntityRendererProvider.Context pContext) {
    super(pContext);
    this.model = new VectorPursuerModel(pContext.bakeLayer(VectorPursuerModel.LAYER_LOCATION));
  }

  public void render(VectorPursuer pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
    pPoseStack.pushPose();
    // Positioning the model at the entity’s current coordinates
    pPoseStack.translate(0.0D, 1.5D, 0.0D); // Adjust Y to match model origin
    pPoseStack.scale(-1, -1, 1);

    if (!pEntity.getDeployed()) {
      AnimationHelper.animate(model, VectorPursuerAnimation.DEPLOY, (pEntity.tickCount + pPartialTick) / 20f, 1, false);
    }
    else {
      AnimationHelper.animate(model, VectorPursuerAnimation.IDLE, (pEntity.tickCount + pPartialTick) / 20f, 1, true);
      float headYaw = Mth.rotLerp(pPartialTick, pEntity.yHeadRotO, pEntity.getYHeadRot());
      model.rootBody.yRot = (headYaw + 180) * ((float) Math.PI / 180F);;
    }

    VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(TEXTURE));
    model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    pPoseStack.popPose();
  }

  public ResourceLocation getTextureLocation(VectorPursuer entity) {return TEXTURE;}
}
