package com.github.ptran779.aegisops.client.model;// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.github.ptran779.aegisops.AegisOps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class DropPodModel extends Model {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "drop_pod_layer"), "main");
	private final ModelPart Body;
	private final ModelPart landGear;
	private final ModelPart landGear2;
	private final ModelPart landGear3;
	private final ModelPart landGear4;
	private final ModelPart doorPanels;
	public final ModelPart leftDoor;
	public final ModelPart rightDoor;

	public DropPodModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);

		this.Body = root.getChild("Body");
		this.landGear = root.getChild("landGear");
		this.landGear2 = root.getChild("landGear2");
		this.landGear3 = root.getChild("landGear3");
		this.landGear4 = root.getChild("landGear4");
		this.doorPanels = root.getChild("doorPanels");
		this.leftDoor = this.doorPanels.getChild("leftDoor");
		this.rightDoor = this.doorPanels.getChild("rightDoor");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-21.0F, -36.0F, -5.0F, 26.0F, 46.0F, 26.0F, new CubeDeformation(0.0F))
		.texOffs(0, 162).addBox(-21.0F, 1.0F, -5.0F, 26.0F, 1.0F, 26.0F, new CubeDeformation(0.0F))
		.texOffs(104, 117).addBox(-14.0F, -41.0F, 2.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(104, 45).addBox(-15.0F, 13.0F, 1.0F, 14.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 72).addBox(-18.0F, 10.0F, -2.0F, 20.0F, 3.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 8.0F, -8.0F));

		PartDefinition landGear = partdefinition.addOrReplaceChild("landGear", CubeListBuilder.create().texOffs(132, 110).addBox(2.0F, 3.5F, -5.25F, 12.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 147).addBox(3.0F, 12.5F, -8.25F, 10.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(132, 62).addBox(-1.0F, -0.5F, -8.25F, 18.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 9.5F, -12.75F));

		PartDefinition cube_r1 = landGear.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(80, 72).addBox(-9.0F, -37.0F, 0.0F, 18.0F, 37.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -0.5F, -8.25F, -0.2094F, 0.0F, 0.0F));

		PartDefinition cube_r2 = landGear.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 154).addBox(-2.0F, 2.0F, -0.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(52, 95).addBox(-3.0F, -4.0F, -1.5F, 6.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 8.0F, -3.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition landGear2 = partdefinition.addOrReplaceChild("landGear2", CubeListBuilder.create().texOffs(144, 134).addBox(2.0F, 3.5F, -5.25F, 12.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(152, 117).addBox(3.0F, 12.5F, -8.25F, 10.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(132, 86).addBox(-1.0F, -0.5F, -8.25F, 18.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.75F, 9.5F, -8.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r3 = landGear2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(104, 0).addBox(-9.0F, -37.0F, 0.0F, 18.0F, 37.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -0.5F, -8.25F, -0.2094F, 0.0F, 0.0F));

		PartDefinition cube_r4 = landGear2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(144, 155).addBox(-2.0F, 2.0F, -0.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(104, 62).addBox(-3.0F, -4.0F, -1.5F, 6.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 8.0F, -3.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition landGear3 = partdefinition.addOrReplaceChild("landGear3", CubeListBuilder.create().texOffs(0, 140).addBox(2.0F, 3.5F, -5.25F, 12.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(144, 148).addBox(3.0F, 12.5F, -8.25F, 10.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(132, 74).addBox(-1.0F, -0.5F, -8.25F, 18.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 9.5F, 12.75F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r5 = landGear3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 95).addBox(-9.0F, -37.0F, 0.0F, 18.0F, 37.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -0.5F, -8.25F, -0.2094F, 0.0F, 0.0F));

		PartDefinition cube_r6 = landGear3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(12, 154).addBox(-2.0F, 2.0F, -0.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(52, 104).addBox(-3.0F, -4.0F, -1.5F, 6.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 8.0F, -3.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition landGear4 = partdefinition.addOrReplaceChild("landGear4", CubeListBuilder.create().texOffs(144, 141).addBox(2.0F, 3.5F, -5.25F, 12.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(152, 124).addBox(3.0F, 12.5F, -8.25F, 10.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(132, 98).addBox(-1.0F, -0.5F, -8.25F, 18.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.75F, 9.5F, 8.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r7 = landGear4.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(52, 117).addBox(-9.0F, -37.0F, 0.0F, 18.0F, 37.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -0.5F, -8.25F, -0.2094F, 0.0F, 0.0F));

		PartDefinition cube_r8 = landGear4.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(156, 0).addBox(-2.0F, 2.0F, -0.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(34, 140).addBox(-3.0F, -4.0F, -1.5F, 6.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 8.0F, -3.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition doorPanels = partdefinition.addOrReplaceChild("doorPanels", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -21.0F, -0.2094F, 0.0F, 0.0F));

		PartDefinition leftDoor = doorPanels.addOrReplaceChild("leftDoor", CubeListBuilder.create().texOffs(104, 134).addBox(0.0F, -35.0F, 0.0F, 9.0F, 35.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, 0.0F, 0.0F));

		PartDefinition rightDoor = doorPanels.addOrReplaceChild("rightDoor", CubeListBuilder.create().texOffs(124, 134).addBox(-9.0F, -35.0F, 0.0F, 9.0F, 35.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		landGear.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		landGear2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		landGear3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		landGear4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		doorPanels.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}