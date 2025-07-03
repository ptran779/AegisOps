package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.model.DropPodModel;
import com.github.ptran779.aegisops.entity.FallingDropPod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class DropPodFallingRender extends EntityRenderer<FallingDropPod> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/block/drop_pod.png");
  private final DropPodModel model;

  public DropPodFallingRender(EntityRendererProvider.Context pContext) {
    super(pContext);
    this.model = new DropPodModel(pContext.bakeLayer(DropPodModel.LAYER_LOCATION));
  }

  @Override
  public void render(FallingDropPod pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
    pPoseStack.pushPose();
    // Positioning the model at the entity’s current coordinates
    pPoseStack.translate(0.0D, 1.5D, 0.0D); // Adjust Y to match model origin
    pPoseStack.scale(-1.0F, -1.0F, 1.0F); // Flip model upright (Forge quirk)

    VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(TEXTURE));
    model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

    pPoseStack.popPose();
  }

  @Override
  public ResourceLocation getTextureLocation(FallingDropPod fallingDropPod) {return TEXTURE;}
}
