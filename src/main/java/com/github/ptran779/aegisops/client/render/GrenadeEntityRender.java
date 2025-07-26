package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.AnimationHelper;
import com.github.ptran779.aegisops.client.ShareModel;
import com.github.ptran779.aegisops.client.animation.GrenadeAnimation;
import com.github.ptran779.aegisops.client.model.GrenadeModel;
import com.github.ptran779.aegisops.entity.extra.Grenade;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GrenadeEntityRender extends EntityRenderer<Grenade> {
  protected static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/item/grenade.png");
  protected final GrenadeModel model;

  public GrenadeEntityRender(EntityRendererProvider.Context pContext) {
    super(pContext);
    this.model = ShareModel.prepGrenadeModel(pContext.bakeLayer(GrenadeModel.LAYER_LOCATION));
  }

  public void render(Grenade pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
    AnimationHelper.animate(model, GrenadeAnimation.DEPLOY, 2.5f, 1, false);
    pPoseStack.pushPose();
    pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntity.getYRot()));
    if (!pEntity.landed) {
      pPoseStack.mulPose(Axis.XP.rotationDegrees((pEntity.tickCount + pPartialTick) * 20f));
    } else {
      pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
    }
    pPoseStack.translate(0, -1.625, 0);

    VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(TEXTURE));
    model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

    pPoseStack.popPose();
  }

  public ResourceLocation getTextureLocation(Grenade grenade) {return TEXTURE;}
}
