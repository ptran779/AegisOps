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

public class GrenadeModel extends AbstractAniModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AegisOps.MOD_ID, "grenade_layer"), "main");
	public static final Map<String, List<String>> BONE_HIERARCHY = Map.ofEntries(
			Map.entry("root", List.of("Main")),
			Map.entry("Main", List.of("Bot", "Top"))
	);
	public GrenadeModel(ModelPart root) {
		super(RenderType::entityCutout, root.getChild("Main"));
		put("Main", root.getChild("Main"));
		put("Bot", get("Main").getChild("Bot"));
		put("Top", get("Main").getChild("Top"));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Main = partdefinition.addOrReplaceChild("Main", CubeListBuilder.create().texOffs(20, 7).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Bot = Main.addOrReplaceChild("Bot", CubeListBuilder.create().texOffs(0, 9).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(20, 0).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(20, 18).addBox(-2.0F, 3.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Top = Main.addOrReplaceChild("Top", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 18).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(20, 13).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public List<String> getBoneChild(String boneName) {return BONE_HIERARCHY.get(boneName);}
	public String getRootBoneName() {return "Main";}
}