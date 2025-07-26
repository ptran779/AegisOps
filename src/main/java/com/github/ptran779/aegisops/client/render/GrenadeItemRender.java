package com.github.ptran779.aegisops.client.render;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.AnimationHelper;
import com.github.ptran779.aegisops.client.ShareModel;
import com.github.ptran779.aegisops.client.animation.GrenadeAnimation;
import com.github.ptran779.aegisops.client.model.GrenadeModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GrenadeItemRender extends BlockEntityWithoutLevelRenderer {
  protected GrenadeModel model;
  protected static final ResourceLocation TEXTURE = new ResourceLocation(AegisOps.MOD_ID, "textures/item/grenade.png");

  public GrenadeItemRender(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
    super(pBlockEntityRenderDispatcher, pEntityModelSet);
    this.model = ShareModel.prepGrenadeModel(pEntityModelSet.bakeLayer(GrenadeModel.LAYER_LOCATION));
  }

  public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                           MultiBufferSource buffer, int light, int overlay) {
    CompoundTag tag = stack.getOrCreateTag();
    if (tag.contains("DeployTick")){
      long deployTick = stack.getOrCreateTag().getLong("DeployTick");
      long currentTick = Minecraft.getInstance().level.getGameTime();
      AnimationHelper.animate(model, GrenadeAnimation.DEPLOY, (float) (currentTick - deployTick) /20, 1, false);
    } else {
      AnimationHelper.animate(model, GrenadeAnimation.DEPLOY, 0f, 1, false);   /// FIXME
    }
    poseStack.pushPose();
    poseStack.translate(0.5, -1, 0.5);
    if (context == ItemDisplayContext.GUI) { // Inventory slot
      poseStack.scale(1.5f, 1.5f, 1.5f); // Scale up 1.5x
      poseStack.mulPose(Axis.XP.rotationDegrees(30));
      poseStack.mulPose(Axis.YP.rotationDegrees(45));
      poseStack.translate(0, -0.5, 0);
    }

    VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
    model.renderToBuffer(poseStack, consumer, light, overlay, 1f, 1f, 1f, 1f);
    poseStack.popPose();
  }
}
