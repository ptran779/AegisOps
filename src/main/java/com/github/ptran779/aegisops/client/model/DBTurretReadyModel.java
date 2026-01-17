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

public class DBTurretReadyModel extends Model {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "dbturret_ready_layer"), "main");
	public final ModelPart Main;
	public final ModelPart Neck;
	public final ModelPart Head;
	public final ModelPart RBarrel;
	public final ModelPart RCtip;
	public final ModelPart LBarrel;
	public final ModelPart LCtip;
	private final ModelPart S1;
	private final ModelPart S2;
	private final ModelPart S3;
	private final ModelPart S4;
	private final ModelPart E1;
	private final ModelPart E2;
	private final ModelPart E3;
	private final ModelPart E4;
	private final ModelPart N1;
	private final ModelPart N2;
	private final ModelPart N3;
	private final ModelPart N4;
	private final ModelPart W1;
	private final ModelPart W2;
	private final ModelPart W3;
	private final ModelPart W4;

	public DBTurretReadyModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.Main = root.getChild("Main");
		this.Neck = this.Main.getChild("Neck");
		this.Head = this.Neck.getChild("Head");
		this.RBarrel = this.Head.getChild("RBarrel");
		this.RCtip = this.RBarrel.getChild("RCtip");
		this.LBarrel = this.Head.getChild("LBarrel");
		this.LCtip = this.LBarrel.getChild("LCtip");
		this.S1 = this.Main.getChild("S1");
		this.S2 = this.S1.getChild("S2");
		this.S3 = this.S2.getChild("S3");
		this.S4 = this.S3.getChild("S4");
		this.E1 = this.Main.getChild("E1");
		this.E2 = this.E1.getChild("E2");
		this.E3 = this.E2.getChild("E3");
		this.E4 = this.E3.getChild("E4");
		this.N1 = this.Main.getChild("N1");
		this.N2 = this.N1.getChild("N2");
		this.N3 = this.N2.getChild("N3");
		this.N4 = this.N3.getChild("N4");
		this.W1 = this.Main.getChild("W1");
		this.W2 = this.W1.getChild("W2");
		this.W3 = this.W2.getChild("W3");
		this.W4 = this.W3.getChild("W4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(24, 20).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

		PartDefinition Neck = Main.addOrReplaceChild("Neck", CubeListBuilder.create().texOffs(0, 20).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(32, 13).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 0.0F));

		PartDefinition Head = Neck.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition RBarrel = Head.addOrReplaceChild("RBarrel", CubeListBuilder.create().texOffs(0, 62).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition RCtip = RBarrel.addOrReplaceChild("RCtip", CubeListBuilder.create().texOffs(52, 62).addBox(-5.0F, -5.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 8.0F, -0.5F));

		PartDefinition LBarrel = Head.addOrReplaceChild("LBarrel", CubeListBuilder.create().texOffs(56, 60).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition LCtip = LBarrel.addOrReplaceChild("LCtip", CubeListBuilder.create().texOffs(48, 27).addBox(-5.0F, -5.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 8.0F, -0.5F));

		PartDefinition S1 = Main.addOrReplaceChild("S1", CubeListBuilder.create().texOffs(64, 14).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 3.0F, 0.1745F, 3.1416F, 0.0F));

		PartDefinition S2 = S1.addOrReplaceChild("S2", CubeListBuilder.create().texOffs(68, 21).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -0.5F, -0.6981F, 0.0F, 0.0F));

		PartDefinition S3 = S2.addOrReplaceChild("S3", CubeListBuilder.create().texOffs(0, 36).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 49).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.6252F, 1.1905F, 0.4363F, 0.0F, 0.0F));

		PartDefinition S4 = S3.addOrReplaceChild("S4", CubeListBuilder.create().texOffs(56, 49).addBox(-3.0F, -10.0F, -0.9F, 6.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition E1 = Main.addOrReplaceChild("E1", CubeListBuilder.create().texOffs(32, 62).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -1.0F, 0.0F, 0.1745F, 1.5708F, 0.0F));

		PartDefinition E2 = E1.addOrReplaceChild("E2", CubeListBuilder.create().texOffs(42, 62).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -0.5F, -0.6981F, 0.0F, 0.0F));

		PartDefinition E3 = E2.addOrReplaceChild("E3", CubeListBuilder.create().texOffs(18, 36).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 49).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.6252F, 1.1905F, 0.4363F, 0.0F, 0.0F));

		PartDefinition E4 = E3.addOrReplaceChild("E4", CubeListBuilder.create().texOffs(54, 38).addBox(-3.0F, -10.0F, -0.9F, 6.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition N1 = Main.addOrReplaceChild("N1", CubeListBuilder.create().texOffs(12, 62).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -3.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition N2 = N1.addOrReplaceChild("N2", CubeListBuilder.create().texOffs(22, 62).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -0.5F, -0.6981F, 0.0F, 0.0F));

		PartDefinition N3 = N2.addOrReplaceChild("N3", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 49).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.6252F, 1.1905F, 0.4363F, 0.0F, 0.0F));

		PartDefinition N4 = N3.addOrReplaceChild("N4", CubeListBuilder.create().texOffs(54, 27).addBox(-3.0F, -10.0F, -0.9F, 6.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition W1 = Main.addOrReplaceChild("W1", CubeListBuilder.create().texOffs(64, 0).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -1.0F, 0.0F, 0.1745F, -1.5708F, 0.0F));

		PartDefinition W2 = W1.addOrReplaceChild("W2", CubeListBuilder.create().texOffs(64, 7).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -0.5F, -0.6981F, 0.0F, 0.0F));

		PartDefinition W3 = W2.addOrReplaceChild("W3", CubeListBuilder.create().texOffs(36, 36).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(42, 49).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.6252F, 1.1905F, 0.4363F, 0.0F, 0.0F));

		PartDefinition W4 = W3.addOrReplaceChild("W4", CubeListBuilder.create().texOffs(50, 0).addBox(-3.0F, -10.5F, -0.9F, 6.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}