package com.github.ptran779.aegisops.client.model;// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.github.ptran779.aegisops.AegisOps;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class DBTurretModel extends AbstractAniModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "dbturret_layer"), "main");
	public static final Map<String, List<String>> BONE_HIERARCHY = Map.ofEntries(
			Map.entry("root", List.of("Main")),
			Map.entry("Main", List.of("Neck", "S1", "E1", "N1", "W1")),
			Map.entry("Neck", List.of("Head")),
			Map.entry("Head", List.of("RBarrel", "LBarrel")),
			Map.entry("RBarrel", List.of("RCtip")),
			Map.entry("LBarrel", List.of("LCtip")),
			Map.entry("S1", List.of("S2")),
			Map.entry("S2", List.of("S3")),
			Map.entry("S3", List.of("S4")),
			Map.entry("E1", List.of("E2")),
			Map.entry("E2", List.of("E3")),
			Map.entry("E3", List.of("E4")),
			Map.entry("N1", List.of("N2")),
			Map.entry("N2", List.of("N3")),
			Map.entry("N3", List.of("N4")),
			Map.entry("W1", List.of("W2")),
			Map.entry("W2", List.of("W3")),
			Map.entry("W3", List.of("W4"))
	);
	public DBTurretModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull, root.getChild("Main"));

		put("Main", root.getChild("Main"));

		put("Neck", get("Main").getChild("Neck"));
		put("Head", get("Neck").getChild("Head"));

		put("RBarrel", get("Head").getChild("RBarrel"));
		put("RCtip", get("RBarrel").getChild("RCtip"));

		put("LBarrel", get("Head").getChild("LBarrel"));
		put("LCtip", get("LBarrel").getChild("LCtip"));

		put("S1", get("Main").getChild("S1"));
		put("S2", get("S1").getChild("S2"));
		put("S3", get("S2").getChild("S3"));
		put("S4", get("S3").getChild("S4"));

		put("E1", get("Main").getChild("E1"));
		put("E2", get("E1").getChild("E2"));
		put("E3", get("E2").getChild("E3"));
		put("E4", get("E3").getChild("E4"));

		put("N1", get("Main").getChild("N1"));
		put("N2", get("N1").getChild("N2"));
		put("N3", get("N2").getChild("N3"));
		put("N4", get("N3").getChild("N4"));

		put("W1", get("Main").getChild("W1"));
		put("W2", get("W1").getChild("W2"));
		put("W3", get("W2").getChild("W3"));
		put("W4", get("W3").getChild("W4"));
	}
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(24, 20).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Neck = Main.addOrReplaceChild("Neck", CubeListBuilder.create().texOffs(0, 20).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(32, 13).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition Head = Neck.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition RBarrel = Head.addOrReplaceChild("RBarrel", CubeListBuilder.create().texOffs(0, 62).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.25F, 0.0F, 0.0F));

		PartDefinition RCtip = RBarrel.addOrReplaceChild("RCtip", CubeListBuilder.create().texOffs(52, 62).addBox(-5.0F, -5.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 3.0F, -0.5F));

		PartDefinition LBarrel = Head.addOrReplaceChild("LBarrel", CubeListBuilder.create().texOffs(56, 60).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.25F, 0.0F, 0.0F));

		PartDefinition LCtip = LBarrel.addOrReplaceChild("LCtip", CubeListBuilder.create().texOffs(48, 27).addBox(-5.0F, -5.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 3.0F, -0.5F));

		PartDefinition S1 = Main.addOrReplaceChild("S1", CubeListBuilder.create().texOffs(64, 14).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 3.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition S2 = S1.addOrReplaceChild("S2", CubeListBuilder.create().texOffs(68, 21).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -0.5F));

		PartDefinition S3 = S2.addOrReplaceChild("S3", CubeListBuilder.create().texOffs(0, 36).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 49).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -0.5F));

		PartDefinition S4 = S3.addOrReplaceChild("S4", CubeListBuilder.create().texOffs(56, 49).addBox(-3.0F, -10.0F, -0.9F, 6.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition E1 = Main.addOrReplaceChild("E1", CubeListBuilder.create().texOffs(32, 62).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -1.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition E2 = E1.addOrReplaceChild("E2", CubeListBuilder.create().texOffs(42, 62).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -0.5F));

		PartDefinition E3 = E2.addOrReplaceChild("E3", CubeListBuilder.create().texOffs(18, 36).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 49).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -0.5F));

		PartDefinition E4 = E3.addOrReplaceChild("E4", CubeListBuilder.create().texOffs(54, 38).addBox(-3.0F, -10.0F, -0.9F, 6.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition N1 = Main.addOrReplaceChild("N1", CubeListBuilder.create().texOffs(12, 62).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -3.0F));

		PartDefinition N2 = N1.addOrReplaceChild("N2", CubeListBuilder.create().texOffs(22, 62).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -0.5F));

		PartDefinition N3 = N2.addOrReplaceChild("N3", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 49).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -0.5F));

		PartDefinition N4 = N3.addOrReplaceChild("N4", CubeListBuilder.create().texOffs(54, 27).addBox(-3.0F, -10.0F, -0.9F, 6.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition W1 = Main.addOrReplaceChild("W1", CubeListBuilder.create().texOffs(64, 0).addBox(-2.0F, -6.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -1.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition W2 = W1.addOrReplaceChild("W2", CubeListBuilder.create().texOffs(64, 7).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -0.5F));

		PartDefinition W3 = W2.addOrReplaceChild("W3", CubeListBuilder.create().texOffs(36, 36).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(42, 49).addBox(-3.0F, -11.0F, -2.0F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, -0.5F));

		PartDefinition W4 = W3.addOrReplaceChild("W4", CubeListBuilder.create().texOffs(50, 0).addBox(-3.0F, -10.5F, -0.9F, 6.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
	public String getRootBoneName() {return "Main";}
	public List<String> getBoneChild(String boneName) {return BONE_HIERARCHY.get(boneName);}
}