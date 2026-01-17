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

public class VectorPursuerModel extends AbstractAniModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "vector_pursuer"), "main");
//	public static final Map<String, List<String>> BONE_HIERARCHY = Map.ofEntries(
//			Map.entry("root", List.of("Main")),
//			Map.entry("Main", List.of("LWing", "RWing", "Tail", "Tail2")),
//			Map.entry("LWing", List.of("Lcore", "LThrust")),
//			Map.entry("RWing", List.of("Rcore", "RThrust")),
//			Map.entry("Tail", List.of("Tcore", "TThrust"))
//	);

	public VectorPursuerModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull, root.getChild("Main"));

		put("Main", root.getChild("Main"));
		put("LWing", get("Main").getChild("LWing"));
		put("RWing", get("Main").getChild("RWing"));
		put("Tail", get("Main").getChild("Tail"));
		put("Tail2", get("Main").getChild("Tail2"));
		put("Lcore", get("LWing").getChild("Lcore"));
		put("LThrust", get("LWing").getChild("LThrust"));
		put("Rcore", get("RWing").getChild("Rcore"));
		put("RThrust", get("RWing").getChild("RThrust"));
		put("Tcore", get("Tail").getChild("Tcore"));
		put("TThrust", get("Tail").getChild("TThrust"));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-2.5F, -1.0F, -4.0F, 5.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 9).addBox(-2.0F, -1.75F, -3.5F, 4.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(22, 14).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(26, 7).addBox(-2.0F, 0.0F, 1.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, -1.0F));

		PartDefinition cube_r1 = Main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0472F));

		PartDefinition cube_r2 = Main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(26, 0).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, -1.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition cube_r3 = Main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 30).addBox(0.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.9F, -3.05F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r4 = Main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(14, 27).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -3.3F, 1.0036F, 0.0F, 0.0F));

		PartDefinition LWing = Main.addOrReplaceChild("LWing", CubeListBuilder.create().texOffs(18, 17).addBox(0.0F, 0.0F, -2.1F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -0.9F, 0.0F, 0.0F, 1.5708F));

		PartDefinition Lcore = LWing.addOrReplaceChild("Lcore", CubeListBuilder.create().texOffs(34, 20).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.6F, -0.1F));

		PartDefinition LThrust = LWing.addOrReplaceChild("LThrust", CubeListBuilder.create().texOffs(22, 27).addBox(-1.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(26, 30).addBox(0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(20, 33).addBox(-1.0F, -0.5F, 0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(26, 33).addBox(-1.0F, -0.5F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.6F, -0.1F));

		PartDefinition RWing = Main.addOrReplaceChild("RWing", CubeListBuilder.create().texOffs(22, 9).addBox(0.0F, -1.0F, -2.1F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -0.9F, 0.0F, 0.0F, 1.5708F));

		PartDefinition RThrust = RWing.addOrReplaceChild("RThrust", CubeListBuilder.create().texOffs(32, 14).addBox(-1.0F, -0.5F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(14, 29).addBox(-1.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(32, 32).addBox(-1.0F, -0.5F, 0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 30).addBox(0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -0.6F, -0.1F));

		PartDefinition Rcore = RWing.addOrReplaceChild("Rcore", CubeListBuilder.create().texOffs(34, 18).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -0.6F, -0.1F));

		PartDefinition Tail = Main.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(18, 22).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition Tcore = Tail.addOrReplaceChild("Tcore", CubeListBuilder.create().texOffs(34, 22).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.4F, 2.0F));

		PartDefinition TThrust = Tail.addOrReplaceChild("TThrust", CubeListBuilder.create().texOffs(28, 27).addBox(0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(14, 32).addBox(-1.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 34).addBox(-1.0F, -0.5F, -1.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(6, 34).addBox(-1.0F, -0.5F, 0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.4F, 2.0F));

		PartDefinition Tail2 = Main.addOrReplaceChild("Tail2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 4.0F));

		PartDefinition cube_r5 = Tail2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(34, 16).addBox(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

//	public List<String> getBoneChild(String boneName) {return BONE_HIERARCHY.get(boneName);}
//	public String getRootBoneName() {return "Main";}
}