package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.block.DropPodBE;
import com.github.ptran779.aegisops.client.model.DropPodModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;

//ChestRenderer

public class DropPodBERender implements BlockEntityRenderer<DropPodBE> {
  private final DropPodModel model;
  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/block/drop_pod.png");

  public DropPodBERender(BlockEntityRendererProvider.Context context) {
    this.model = new DropPodModel(context.bakeLayer(DropPodModel.LAYER_LOCATION));
  }


  @Override
  public void render(DropPodBE entity, float partialTick, PoseStack poseStack,
                     MultiBufferSource buffer, int packedLight, int packedOverlay) {
    float angle = (float) (entity.openStep / (float) DropPodBE.doorOpenTime * Math.toRadians(80.0F));

    model.leftDoor.yRot = angle;
    model.rightDoor.yRot = -angle;

    poseStack.pushPose();

    poseStack.translate(0.5, 1.5, 0.5);  // Center and raise model
    poseStack.scale(1.0F, -1.0F, 1.0F);  // Flip Y for Minecraft convention

    VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
    model.renderToBuffer(poseStack, builder, packedLight, packedOverlay, 1, 1, 1, 1);

    poseStack.popPose();
  }
}
