package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.AnimationHelper;
import com.github.ptran779.aegisops.client.animation.HellPodAnimation;
import com.github.ptran779.aegisops.client.animation.ModularShieldAnimation;
import com.github.ptran779.aegisops.client.model.ModularShieldModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ModularShieldRender extends BlockEntityWithoutLevelRenderer {
//  public static final ModularShieldRender INSTANCE = new ModularShieldRender();
  private ModularShieldModel model;
  private static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/item/modular_shield.png");

  public ModularShieldRender(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
    super(pBlockEntityRenderDispatcher, pEntityModelSet);
    this.model = new ModularShieldModel(pEntityModelSet.bakeLayer(ModularShieldModel.LAYER_LOCATION));
  }

  public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                           MultiBufferSource buffer, int light, int overlay) {
    CompoundTag tag = stack.getOrCreateTag();
    if (tag.contains("DeployTick")){
      long deployTick = stack.getOrCreateTag().getLong("DeployTick");
      long currentTick = Minecraft.getInstance().level.getGameTime();
      AnimationHelper.animate(model, ModularShieldAnimation.DEPLOY, (float) (currentTick - deployTick) /20, 1, false);
    } else {
      AnimationHelper.animate(model, ModularShieldAnimation.DEPLOY, (float) 0, 1, false);   /// FIXME
    }

    poseStack.pushPose();
    switch (context) {
      case FIRST_PERSON_LEFT_HAND -> {
        poseStack.translate(0.48, -0.45, 0.65);
        poseStack.mulPose(Axis.YP.rotationDegrees(0)); // <- 90 + your original 90
      }
      case FIRST_PERSON_RIGHT_HAND -> {
        poseStack.translate(0.52, -0.45, 0.65);
        poseStack.mulPose(Axis.YP.rotationDegrees(0));   // <- -90 + 90 = 0
      }
      case THIRD_PERSON_LEFT_HAND -> {
        poseStack.translate(0.48, -0.45, 0.65);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));  // original left hand rotation
      }
      case THIRD_PERSON_RIGHT_HAND -> {
        poseStack.translate(0.52, -0.45, 0.65);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90)); // original right hand rotation
      }
      default -> {
        poseStack.translate(0.5, 0, 0.6);
        poseStack.scale(0.6f, 0.6f, 0.6f);
      }
    }
    VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
    model.renderToBuffer(poseStack, consumer, light, overlay, 1f, 1f, 1f, 1f);

    poseStack.popPose();
  }
}
