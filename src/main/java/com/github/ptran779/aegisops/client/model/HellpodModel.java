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

public class HellpodModel extends AbstractAniModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "hellpod_layer"), "main");
	public static final Map<String, List<String>> BONE_HIERARCHY = Map.ofEntries(
			Map.entry("root", List.of("Main")),
			Map.entry("Main", List.of("Shell", "Lift", "Fins")),
			Map.entry("Fins", List.of("n1", "s1", "e1", "w1", "EHatch", "WHatch"))
	);

	public HellpodModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull, root.getChild("Main"));

		put("Main", root.getChild("Main"));
		put("Shell", get("Main").getChild("Shell"));
		put("Lift", get("Main").getChild("Lift"));
		put("Fins", get("Main").getChild("Fins"));
		put("e1", get("Fins").getChild("e1"));
		put("n1", get("Fins").getChild("n1"));
		put("s1", get("Fins").getChild("s1"));
		put("w1", get("Fins").getChild("w1"));
		put("EHatch", get("Main").getChild("EHatch"));
		put("WHatch", get("Main").getChild("WHatch"));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Shell = Main.addOrReplaceChild("Shell", CubeListBuilder.create().texOffs(0, 32).addBox(10.75F, 0.0F, -19.75F, 1.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(124, 39).addBox(11.75F, 3.0F, -16.75F, 1.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(34, 32).addBox(-11.75F, 0.0F, -19.75F, 1.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(102, 39).addBox(-12.75F, 3.0F, -16.75F, 1.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 107).addBox(-8.0F, 0.0F, -23.5F, 16.0F, 16.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(42, 17).addBox(-5.0F, 3.0F, -24.5F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 32).addBox(8.0F, 21.0F, -20.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(4, 35).addBox(8.0F, 21.0F, -3.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 64).addBox(8.0F, 18.0F, -3.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(8.0F, -7.0F, -3.75F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(158, 0).addBox(8.0F, -2.0F, -3.75F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 86).addBox(8.0F, 18.0F, -21.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 24).addBox(8.0F, -7.0F, -21.75F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(158, 23).addBox(8.0F, -2.0F, -22.75F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-9.0F, 21.0F, -3.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 69).addBox(-10.0F, 18.0F, -3.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 7).addBox(-10.0F, -7.0F, -3.75F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(146, 0).addBox(-11.0F, -2.0F, -3.75F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(146, 23).addBox(-11.0F, -2.0F, -22.75F, 3.0F, 20.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 17).addBox(-10.0F, -7.0F, -21.75F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 81).addBox(-10.0F, 18.0F, -21.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 35).addBox(-9.0F, 21.0F, -20.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(104, 22).addBox(-8.0F, 0.0F, -1.0F, 16.0F, 16.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(18, 33).addBox(-5.0F, 3.0F, 0.0F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(-2.0F, 30.0F, -13.75F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(48, 0).addBox(-4.0F, 28.0F, -15.75F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(56, 17).addBox(-6.0F, 26.0F, -17.75F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-8.0F, 25.0F, -19.75F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, 11.75F));

		PartDefinition cube_r1 = Shell.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(68, 57).addBox(-5.0F, -9.0F, -2.0F, 10.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(112, 83).addBox(-8.0F, -10.0F, -1.0F, 16.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r2 = Shell.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(68, 31).addBox(-1.0F, -10.0F, -8.0F, 1.0F, 10.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 25.0F, -11.75F, 0.0F, 0.0F, -0.3927F));

		PartDefinition cube_r3 = Shell.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 64).addBox(0.0F, -10.0F, -8.0F, 1.0F, 10.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 25.0F, -11.75F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r4 = Shell.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(112, 0).addBox(-8.0F, -10.0F, 0.0F, 16.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 25.0F, -4.75F, -0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r5 = Shell.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(112, 11).addBox(-8.0F, -10.0F, -1.0F, 16.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 25.0F, -18.75F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r6 = Shell.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(140, 65).addBox(0.0F, -9.0F, -5.5F, 2.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 81).addBox(0.0F, -10.0F, -8.5F, 1.0F, 10.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.75F, 0.0F, -11.25F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r7 = Shell.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(116, 65).addBox(-2.0F, -9.0F, -5.0F, 2.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(82, 57).addBox(-1.0F, -10.0F, -8.0F, 1.0F, 10.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.75F, 0.0F, -11.75F, 0.0F, 0.0F, -0.3927F));

		PartDefinition cube_r8 = Shell.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(52, 32).addBox(-5.0F, -9.0F, 0.0F, 10.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(112, 94).addBox(-8.0F, -10.0F, 0.0F, 16.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -23.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition Lift = Main.addOrReplaceChild("Lift", CubeListBuilder.create().texOffs(0, 17).addBox(-7.0F, 1.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition EHatch = Main.addOrReplaceChild("EHatch", CubeListBuilder.create().texOffs(0, 64).addBox(1.0F, -1.0F, -8.0F, 8.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, -25.75F, 0.0F));

		PartDefinition WHatch = Main.addOrReplaceChild("WHatch", CubeListBuilder.create().texOffs(64, 0).addBox(-8.0F, -1.0F, -8.0F, 8.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -25.75F, 0.0F));

		PartDefinition Fins = Main.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition n1 = Fins.addOrReplaceChild("n1", CubeListBuilder.create().texOffs(18, 86).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(82, 88).addBox(-5.0F, 0.0F, -5.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 125).addBox(-7.0F, -1.0F, -10.0F, 14.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, -8.0F));

		PartDefinition s1 = Fins.addOrReplaceChild("s1", CubeListBuilder.create().texOffs(82, 83).addBox(-5.0F, 0.0F, 1.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 136).addBox(-7.0F, -1.0F, 0.0F, 14.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(18, 81).addBox(-5.0F, -2.0F, 1.0F, 10.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 8.0F));

		PartDefinition w1 = Fins.addOrReplaceChild("w1", CubeListBuilder.create().texOffs(38, 120).addBox(1.0F, 0.0F, -5.0F, 4.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(34, 105).addBox(0.0F, -1.0F, -7.0F, 10.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(72, 104).addBox(1.0F, -2.0F, -5.0F, 4.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -18.0F, 0.0F));

		PartDefinition e1 = Fins.addOrReplaceChild("e1", CubeListBuilder.create().texOffs(72, 93).addBox(-5.0F, -2.0F, -5.0F, 4.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(38, 131).addBox(-5.0F, 0.0F, -5.0F, 4.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(34, 90).addBox(-10.0F, -1.0F, -7.0F, 10.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -18.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}
	public String getRootBoneName() {return "Main";}
	public List<String> getBoneChild(String boneName) {return BONE_HIERARCHY.get(boneName);}
}