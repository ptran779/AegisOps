package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.model.TurretBulletModel;
import com.github.ptran779.aegisops.entity.TurretBullet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class TurretBulletRender extends EntityRenderer<TurretBullet> {
  private final TurretBulletModel model;
  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/entities/bullet.png");

  public TurretBulletRender(EntityRendererProvider.Context pContext) {
    super(pContext);
    this.model = new TurretBulletModel(pContext.bakeLayer(TurretBulletModel.LAYER_LOCATION));
  }

  @Override
  public void render(TurretBullet pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
    pPoseStack.pushPose();

    pPoseStack.scale(1.0F, 1.0F, -1.0F); // Flip Y and Z if needed

    // 2. Rotate based on motion vector
    Vec3 motion = pEntity.getDeltaMovement();
    float xRot = (float)(Math.toDegrees(Math.atan2(motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
    float yRot = (float)(Math.toDegrees(Math.atan2(-motion.x, motion.z)));

    pPoseStack.mulPose(Axis.YP.rotationDegrees(yRot));
    pPoseStack.mulPose(Axis.XP.rotationDegrees(xRot));
    pPoseStack.translate(0.0D, 0.0625, 0.0D); // correct traj offset

    VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(TEXTURE));
    model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

    pPoseStack.popPose();
  }

  @Override
  public ResourceLocation getTextureLocation(TurretBullet turretBullet) {
    return TEXTURE;
  }
}
