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

public class ModularShieldModel extends AbstractAniModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "modular_shield_layer"), "main");
	public static final Map<String, List<String>> BONE_HIERARCHY = Map.ofEntries(
			Map.entry("root", List.of("Main")),
			Map.entry("Main", List.of("Handle","Plate")),
			Map.entry("Plate", List.of("LPan","RPan","BPan","TPan"))
	);

	public ModularShieldModel(ModelPart root) {
    super(RenderType::entityCutoutNoCull, root.getChild("Main"));

		put("Main", root.getChild("Main"));
		put("Handle", get("Main").getChild("Handle"));
		put("Plate", get("Main").getChild("Plate"));
		put("LPan", get("Plate").getChild("LPan"));
		put("RPan", get("Plate").getChild("RPan"));
		put("BPan", get("Plate").getChild("BPan"));
		put("TPan", get("Plate").getChild("TPan"));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		PartDefinition Plate = Main.addOrReplaceChild("Plate", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -3.0F, 10.0F, 18.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, -1.0F));
		PartDefinition LPan = Plate.addOrReplaceChild("LPan", CubeListBuilder.create().texOffs(12, 32).addBox(0.0F, -6.0F, -0.5F, 5.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(24, 0).addBox(5.0F, -8.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, -2.0F, 0.0F, -0.3927F, 0.0F));
		PartDefinition RPan = Plate.addOrReplaceChild("RPan", CubeListBuilder.create().texOffs(32, 0).addBox(-7.0F, -8.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-5.0F, -6.0F, -0.5F, 5.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, -2.0F, 0.0F, 0.3927F, 0.0F));
		PartDefinition BPan = Plate.addOrReplaceChild("BPan", CubeListBuilder.create().texOffs(16, 20).addBox(-4.5F, -5.0F, 0.0F, 9.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, -1.5F, -2.7489F, 0.0F, 0.0F));
		PartDefinition TPan = Plate.addOrReplaceChild("TPan", CubeListBuilder.create().texOffs(16, 26).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, -1.5F, 2.7489F, 0.0F, 0.0F));
		PartDefinition Handle = Main.addOrReplaceChild("Handle", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, -1.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	public String getRootBoneName() {return "Main";}
	public List<String> getBoneChild(String boneName) {return BONE_HIERARCHY.get(boneName);}
}