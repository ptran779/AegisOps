package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.AnimationHelper;
import com.github.ptran779.aegisops.client.animation.DBTurretAnimation;
import com.github.ptran779.aegisops.client.model.DBTurretModel;
import com.github.ptran779.aegisops.client.model.DBTurretReadyModel;
import com.github.ptran779.aegisops.entity.structure.DBTurret;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class DBTurretRender extends EntityRenderer<DBTurret> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/entities/dbturret.png");
  private final DBTurretModel model;
  private final DBTurretReadyModel modelReady;

  public DBTurretRender(EntityRendererProvider.Context pContext) {
    super(pContext);
    this.model = new DBTurretModel(pContext.bakeLayer(DBTurretModel.LAYER_LOCATION));
    this.modelReady = new DBTurretReadyModel(pContext.bakeLayer(DBTurretReadyModel.LAYER_LOCATION));
  }

  public void render(DBTurret pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
    pPoseStack.pushPose();
    // Positioning the model at the entity’s current coordinates
    pPoseStack.translate(0.0D, 1.8D, 0.0D); // Adjust Y to match model origin
    pPoseStack.scale(-1.25F, -1.25F, 1.25F); // Flip model + make it slightly bigger
    pPoseStack.mulPose(Axis.YP.rotationDegrees(180));

    // if deployed already
    if (!pEntity.getEntityData().get(DBTurret.DEPLOYED)){
      float time = Math.max((pEntity.tickCount + pPartialTick + DBTurret.T_OFFSET) / 20f, 0);    // seconds = ticks/20
      AnimationHelper.animate(model, DBTurretAnimation.deploy, time, 1, false);

      VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(TEXTURE));
      model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    } else {
      modelReady.Main.getAllParts().forEach(ModelPart::resetPose);  // clean animation
      // modelReady
      modelReady.Head.xRot = (float) Math.toRadians(pEntity.getXRot());
      modelReady.Neck.yRot = (float) Math.toRadians(pEntity.getYHeadRot());

      // animate barrel shooting
      if (pEntity.cannonProgress < 16){
        pEntity.cannonProgress += pPartialTick;
        // pick the cannon barrel
        ModelPart cTip = pEntity.getEntityData().get(DBTurret.LEFT_BARREL) ? modelReady.LCtip: modelReady.RCtip;
        ModelPart cBarrel = pEntity.getEntityData().get(DBTurret.LEFT_BARREL) ? modelReady.LBarrel: modelReady.RBarrel;
        if (pEntity.cannonProgress < 2){
          cTip.y -= pEntity.cannonProgress / 2 * 5;
          cBarrel.z += pEntity.cannonProgress / 2 * 1;
        } else if (pEntity.cannonProgress < 16){
          cTip.y -= (1-(pEntity.cannonProgress-2)/14) * 5;
          cBarrel.z += (1-(pEntity.cannonProgress-2)/14) * 1;
        }}

      VertexConsumer vertexConsumer = pBuffer.getBuffer(modelReady.renderType(TEXTURE));
      modelReady.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
    pPoseStack.popPose();
  }

  @Override
  public ResourceLocation getTextureLocation(DBTurret turret) {return TEXTURE;}
}
