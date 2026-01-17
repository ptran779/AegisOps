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

public class BeaconModel extends AbstractAniModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "beacon_layer"), "main");

//	public static final Map<String, List<String>> BONE_HIERARCHY = Map.ofEntries(
//			Map.entry("root", List.of("Body")),
//			Map.entry("Body", List.of("Leg1", "Leg2", "Leg3", "Leg4", "head")),
//			Map.entry("Leg1", List.of("InnerLeg")),
//			Map.entry("InnerLeg", List.of("Feet")),
//			Map.entry("Leg2", List.of("InnerLeg2")),
//			Map.entry("InnerLeg2", List.of("Feet2")),
//			Map.entry("Leg3", List.of("InnerLeg3")),
//			Map.entry("InnerLeg3", List.of("Feet3")),
//			Map.entry("Leg4", List.of("InnerLeg4")),
//			Map.entry("InnerLeg4", List.of("Feet4")),
//			Map.entry("head", List.of("Fin1", "Fin2", "Fin3", "Fin4", "Atena")),
//			Map.entry("Fin1", List.of("bone", "bone2", "bone3")),
//			Map.entry("Fin2", List.of("bone4", "bone5", "bone6")),
//			Map.entry("Fin3", List.of("bone7", "bone8", "bone9")),
//			Map.entry("Fin4", List.of("bone10", "bone11", "bone12")),
//			Map.entry("Atena", List.of("Atena2"))
//	);

	public BeaconModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull, root.getChild("Body"));

		put("Body", root.getChild("Body"));

		put("Leg1", get("Body").getChild("Leg1"));
		put("InnerLeg", get("Leg1").getChild("InnerLeg"));
		put("Feet", get("InnerLeg").getChild("Feet"));

		put("Leg2", get("Body").getChild("Leg2"));
		put("InnerLeg2", get("Leg2").getChild("InnerLeg2"));
		put("Feet2", get("InnerLeg2").getChild("Feet2"));

		put("Leg3", get("Body").getChild("Leg3"));
		put("InnerLeg3", get("Leg3").getChild("InnerLeg3"));
		put("Feet3", get("InnerLeg3").getChild("Feet3"));

		put("Leg4", get("Body").getChild("Leg4"));
		put("InnerLeg4", get("Leg4").getChild("InnerLeg4"));
		put("Feet4", get("InnerLeg4").getChild("Feet4"));

		put("head", get("Body").getChild("head"));

		put("Fin1", get("head").getChild("Fin1"));
		put("bone", get("Fin1").getChild("bone"));
		put("bone2", get("Fin1").getChild("bone2"));
		put("bone3", get("Fin1").getChild("bone3"));

		put("Fin2", get("head").getChild("Fin2"));
		put("bone4", get("Fin2").getChild("bone4"));
		put("bone5", get("Fin2").getChild("bone5"));
		put("bone6", get("Fin2").getChild("bone6"));

		put("Fin3", get("head").getChild("Fin3"));
		put("bone7", get("Fin3").getChild("bone7"));
		put("bone8", get("Fin3").getChild("bone8"));
		put("bone9", get("Fin3").getChild("bone9"));

		put("Fin4", get("head").getChild("Fin4"));
		put("bone10", get("Fin4").getChild("bone10"));
		put("bone11", get("Fin4").getChild("bone11"));
		put("bone12", get("Fin4").getChild("bone12"));

		put("Atena", get("head").getChild("Atena"));
		put("Atena2", get("Atena").getChild("Atena2"));
	}
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(54, 54).addBox(-2.0F, 0.0F, 2.0F, 4.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(34, 54).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(44, 51).addBox(-3.0F, 0.0F, -2.0F, 1.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(24, 51).addBox(2.0F, 0.0F, -2.0F, 1.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Leg1 = Body.addOrReplaceChild("Leg1", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.0F));

		PartDefinition InnerLeg = Leg1.addOrReplaceChild("InnerLeg", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 1.1F));

		PartDefinition Feet = InnerLeg.addOrReplaceChild("Feet", CubeListBuilder.create().texOffs(38, 10).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -2.0F));

		PartDefinition Leg2 = Body.addOrReplaceChild("Leg2", CubeListBuilder.create().texOffs(14, 22).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition InnerLeg2 = Leg2.addOrReplaceChild("InnerLeg2", CubeListBuilder.create().texOffs(0, 39).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 1.1F));

		PartDefinition Feet2 = InnerLeg2.addOrReplaceChild("Feet2", CubeListBuilder.create().texOffs(38, 14).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -2.0F));

		PartDefinition Leg3 = Body.addOrReplaceChild("Leg3", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition InnerLeg3 = Leg3.addOrReplaceChild("InnerLeg3", CubeListBuilder.create().texOffs(8, 39).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 1.1F));

		PartDefinition Feet3 = InnerLeg3.addOrReplaceChild("Feet3", CubeListBuilder.create().texOffs(38, 18).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -2.0F));

		PartDefinition Leg4 = Body.addOrReplaceChild("Leg4", CubeListBuilder.create().texOffs(24, 11).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition InnerLeg4 = Leg4.addOrReplaceChild("InnerLeg4", CubeListBuilder.create().texOffs(16, 42).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 1.1F));

		PartDefinition Feet4 = InnerLeg4.addOrReplaceChild("Feet4", CubeListBuilder.create().texOffs(24, 42).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, -2.0F));

		PartDefinition head = Body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Fin1 = head.addOrReplaceChild("Fin1", CubeListBuilder.create().texOffs(28, 30).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.75F, -0.1F));

		PartDefinition bone = Fin1.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(17, 33).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.9F));

		PartDefinition bone2 = Fin1.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(44, 28).addBox(-1.9F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 1.4F));

		PartDefinition bone3 = Fin1.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(44, 32).addBox(-0.1F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 1.4F));

		PartDefinition Fin2 = head.addOrReplaceChild("Fin2", CubeListBuilder.create().texOffs(0, 33).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.75F, 0.1F, 0.0F, 3.1416F, 0.0F));

		PartDefinition bone4 = Fin2.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(39, 42).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.9F));

		PartDefinition bone5 = Fin2.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(9, 49).addBox(-1.9F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 1.4F));

		PartDefinition bone6 = Fin2.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(46, 0).addBox(-0.1F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 1.4F));

		PartDefinition Fin3 = head.addOrReplaceChild("Fin3", CubeListBuilder.create().texOffs(16, 36).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, -3.75F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition bone7 = Fin3.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(45, 22).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.9F));

		PartDefinition bone8 = Fin3.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(46, 4).addBox(-1.9F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 1.4F));

		PartDefinition bone9 = Fin3.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(9, 53).addBox(-0.1F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 1.4F));

		PartDefinition Fin4 = head.addOrReplaceChild("Fin4", CubeListBuilder.create().texOffs(32, 36).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, -3.75F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition bone10 = Fin4.addOrReplaceChild("bone10", CubeListBuilder.create().texOffs(45, 25).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.9F));

		PartDefinition bone11 = Fin4.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(0, 55).addBox(-1.9F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 1.4F));

		PartDefinition bone12 = Fin4.addOrReplaceChild("bone12", CubeListBuilder.create().texOffs(0, 59).addBox(-0.1F, 0.0F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 1.4F));

		PartDefinition Atena = head.addOrReplaceChild("Atena", CubeListBuilder.create().texOffs(28, 22).addBox(-2.0F, -3.75F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Atena2 = Atena.addOrReplaceChild("Atena2", CubeListBuilder.create().texOffs(0, 49).addBox(-1.0F, -3.75F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}
//	public String getRootBoneName() {return "Body";}
//	public List<String> getBoneChild(String boneName) {return BONE_HIERARCHY.get(boneName);}
}