package com.github.ptran779.aegisops.client.model;


import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.client.IBoneHierachy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortDispModel extends Model implements IBoneHierachy {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "dispenser_layer"), "main");
	public final Map<String, ModelPart> BONE_PARTS = new HashMap<>();
	private final ModelPart bone;
	public static final Map<String, List<String>> BONE_HIERARCHY = Map.ofEntries(
			Map.entry("root", List.of("bone")),
			Map.entry("bone", List.of("LeftSlot", "RightSlot", "BottomSlot", "TopPanel2", "TopPanel1", "EPan","WPan","SPan","NPan"))
	);

	public PortDispModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		bone = root.getChild("bone");
		put("bone", root.getChild("bone"));
		put("LeftSlot", get("bone").getChild("LeftSlot"));
		put("RightSlot", get("bone").getChild("RightSlot"));
		put("BottomSlot", get("bone").getChild("BottomSlot"));
		put("TopPanel2", get("bone").getChild("TopPanel2"));
		put("TopPanel1", get("bone").getChild("TopPanel1"));

		put("EPan", get("bone").getChild("EPan"));
		put("WPan", get("bone").getChild("WPan"));
		put("SPan", get("bone").getChild("SPan"));
		put("NPan", get("bone").getChild("NPan"));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition LeftSlot = bone.addOrReplaceChild("LeftSlot", CubeListBuilder.create().texOffs(0, 61).addBox(-2.0F, -2.0F, -7.0F, 8.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(27, 77).addBox(-1.5F, -4.0F, -6.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(27, 80).addBox(-1.5F, -4.0F, -3.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(27, 83).addBox(-1.5F, -4.0F, 2.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(27, 86).addBox(-1.5F, -4.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 55).addBox(-1.5F, -4.0F, 5.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 77).addBox(4.5F, -4.0F, -6.5F, 1.0F, 2.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(28, 90).addBox(1.5F, -4.0F, -6.5F, 1.0F, 2.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(56, 90).addBox(-1.5F, -4.0F, -6.5F, 1.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -8.0F, 0.0F));

		PartDefinition RightSlot = bone.addOrReplaceChild("RightSlot", CubeListBuilder.create().texOffs(60, 17).addBox(-6.0F, -2.0F, -7.0F, 8.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(112, 89).addBox(-5.5F, -4.0F, -6.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 92).addBox(-5.5F, -4.0F, -3.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 98).addBox(-5.5F, -4.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 95).addBox(-5.5F, -4.0F, 2.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 101).addBox(-5.5F, -4.0F, 5.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(84, 90).addBox(0.5F, -4.0F, -6.5F, 1.0F, 2.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(0, 92).addBox(-2.5F, -4.0F, -6.5F, 1.0F, 2.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(96, 59).addBox(-5.5F, -4.0F, -6.5F, 1.0F, 2.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -8.0F, 0.0F));

		PartDefinition BottomSlot = bone.addOrReplaceChild("BottomSlot", CubeListBuilder.create().texOffs(0, 17).addBox(-6.0F, 3.0F, -7.0F, 16.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(28, 105).addBox(-5.0F, 0.0F, -6.0F, 2.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(104, 9).addBox(-1.0F, 0.0F, -6.0F, 2.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(96, 74).addBox(3.0F, 0.0F, -6.0F, 2.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(102, 33).addBox(7.0F, 0.0F, -6.0F, 2.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(64, 9).addBox(-5.0F, 0.0F, 2.0F, 14.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(102, 48).addBox(-5.0F, 0.0F, -6.0F, 14.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -8.0F, 0.0F));

		PartDefinition TopPanel2 = bone.addOrReplaceChild("TopPanel2", CubeListBuilder.create().texOffs(44, 61).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 0.0F));

		PartDefinition TopPanel1 = bone.addOrReplaceChild("TopPanel1", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -1.0F, 0.0F, 16.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 0.0F));

		PartDefinition EPan = bone.addOrReplaceChild("EPan", CubeListBuilder.create().texOffs(78, 105).addBox(-1.0F, -6.25F, -5.0F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(70, 70).addBox(-1.0F, -7.25F, -6.0F, 1.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 33).addBox(0.0F, -9.25F, -8.0F, 1.0F, 12.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, -3.75F, 0.0F));

		PartDefinition WPan = bone.addOrReplaceChild("WPan", CubeListBuilder.create().texOffs(34, 33).addBox(-2.0F, -9.25F, -8.0F, 1.0F, 12.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(44, 70).addBox(-1.0F, -7.25F, -6.0F, 1.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(56, 105).addBox(-1.0F, -6.25F, -5.0F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(10.0F, -3.75F, 0.0F));

		PartDefinition SPan = bone.addOrReplaceChild("SPan", CubeListBuilder.create().texOffs(104, 24).addBox(-6.0F, -4.0F, -1.0F, 12.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 107).addBox(-5.0F, -3.0F, -1.0F, 10.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(68, 33).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 10.0F));

		PartDefinition NPan = bone.addOrReplaceChild("NPan", CubeListBuilder.create().texOffs(68, 46).addBox(-8.0F, -6.0F, 1.0F, 16.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(100, 105).addBox(-6.0F, -4.0F, 0.0F, 12.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 0).addBox(-5.0F, -3.0F, 0.0F, 10.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, -10.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}
	private void put(String name, ModelPart part) {BONE_PARTS.put(name, part);}
	public ModelPart get(String name) {return BONE_PARTS.get(name);}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public String getRootBoneName() {return "bone";}

	@Override
	public Map<String, List<String>> getFullBoneHierachy() {return BONE_HIERARCHY;}

	@Override
	public List<String> getBoneChild(String boneName) {return BONE_HIERARCHY.get(boneName);}

	@Override
	public ModelPart getRoot() {return bone;}
}