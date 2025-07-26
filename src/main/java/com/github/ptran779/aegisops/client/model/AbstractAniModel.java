package com.github.ptran779.aegisops.client.model;

import com.github.ptran779.aegisops.client.IBoneHierachy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractAniModel extends Model implements IBoneHierachy {
  protected final Map<String, ModelPart> BONE_PARTS = new HashMap<>();
  public final ModelPart rootBody;

  public AbstractAniModel(Function<ResourceLocation, RenderType> pRenderType, ModelPart rootBody) {
    super(pRenderType);
    this.rootBody = rootBody;
  }

  public ModelPart getRoot() {return rootBody;}
  public void put(String name, ModelPart part) {BONE_PARTS.put(name, part);}
  public ModelPart get(String name) {return BONE_PARTS.get(name);}
  public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    rootBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}
