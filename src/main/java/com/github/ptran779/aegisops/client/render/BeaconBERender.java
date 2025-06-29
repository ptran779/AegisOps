package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.block.BeaconBE;
import com.github.ptran779.aegisops.client.RecursiveAnimationHelper;
import com.github.ptran779.aegisops.client.animation.BeaconAnimation;
import com.github.ptran779.aegisops.client.model.BeaconModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class BeaconBERender implements BlockEntityRenderer<BeaconBE> {
  private static final float[] rgb = new float[]{0.5F, 0.8F, 1.0F};
  private final BeaconModel model;
  private final Vector3f animationScratch = new Vector3f();

  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/block/beacon.png");
//  private final AnimationState animationState = new AnimationState();

  public BeaconBERender(BlockEntityRendererProvider.Context context) {
    this.model = new BeaconModel(context.bakeLayer(BeaconModel.LAYER_LOCATION));
  }

  @Override
  public void render(BeaconBE entity, float partialTick, PoseStack poseStack,
                     MultiBufferSource buffer, int packedLight, int packedOverlay) {
    poseStack.pushPose();
    poseStack.translate(0.5, 1.5, 0.5); // Center and lift the model
    poseStack.scale(1.0F, -1.0F, -1.0F); // Flip Y and Z if needed
    VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));

    float time = (entity.step + partialTick) / 20f;    // seconds = ticks/20
    RecursiveAnimationHelper.animate(model, BeaconAnimation.beaconDeploy, time, 1, animationScratch);
    // 3. Render model
    float alpha = 1f;
    if (entity.step> 1060 && entity.step < 1260){ // alpha fade out
      alpha = Mth.clamp(1.0f - (entity.step - 1060) / 200.0f, 0.0f, 1.0f);
    }
    model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, 1f, 1f, 1f, alpha);
    poseStack.popPose();
    // beam
    if (entity.step >= 160 && entity.step <= 1060) {
      BeaconRenderer.renderBeaconBeam(poseStack, buffer, BeaconRenderer.BEAM_LOCATION, partialTick,1.0F, entity.getLevel().getGameTime(),1, 100, rgb, 0.1F, 0.12F);
    }
  }
}
