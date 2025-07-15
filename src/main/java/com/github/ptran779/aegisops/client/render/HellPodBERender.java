package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.block.HellPodBE;
import com.github.ptran779.aegisops.client.AnimationHelper;
import com.github.ptran779.aegisops.client.animation.HellPodAnimation;
import com.github.ptran779.aegisops.client.model.HellpodModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

//ChestRenderer

public class HellPodBERender implements BlockEntityRenderer<HellPodBE> {
  private final HellpodModel model;
  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/block/hell_pod.png");

  public HellPodBERender(BlockEntityRendererProvider.Context context) {
    this.model = new HellpodModel(context.bakeLayer(HellpodModel.LAYER_LOCATION));
  }


  @Override
  public void render(HellPodBE bEntity, float partialTick, PoseStack poseStack,
                     MultiBufferSource buffer, int packedLight, int packedOverlay) {
    poseStack.pushPose();

    poseStack.translate(0.5, 1.88125, 0.5);
    poseStack.scale(1.0F, -1.0F, 1.0F);  // Flip Y for Minecraft convention

    float aniTick = (bEntity.stepCounter + partialTick);
    AnimationHelper.animate(model, HellPodAnimation.DEPLOY2, aniTick / 20, 1);

    VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
    model.renderToBuffer(poseStack, builder, packedLight, packedOverlay, 1, 1, 1, 1);

    poseStack.popPose();
  }
}
