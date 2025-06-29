package com.github.ptran779.aegisops.client.animation;// Save this class in your mod and generate all required imports

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Made with Blockbench 4.12.4
 * Exported for Minecraft version 1.19 or later with Mojang mappings
 * @author Author
 */
public class BeaconAnimation {
	public static final AnimationDefinition beaconDeploy = AnimationDefinition.Builder.withLength(6.0F)
		.addAnimation("InnerLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("InnerLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(1.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.posVec(0.0F, -4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.posVec(0.0F, -7.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Feet", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Body", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(2.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.posVec(0.0F, 3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.posVec(0.0F, 3.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(4.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.degreeVec(40.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin1", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 3.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin1", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone2", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone3", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(4.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.degreeVec(40.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin2", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.posVec(0.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin2", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone4", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone5", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone6", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(4.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.degreeVec(40.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin3", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.posVec(3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin3", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone7", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone8", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone9", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone9", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(4.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.degreeVec(40.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin4", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.posVec(-3.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Fin4", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(4.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone10", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone10", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 2.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone11", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone11", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone12", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(5.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("bone12", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.5F, KeyframeAnimations.posVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Atena", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(4.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.posVec(0.0F, 4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Atena", new AnimationChannel(AnimationChannel.Targets.SCALE,
				new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(5.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Atena2", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(5.5F, KeyframeAnimations.posVec(0.0F, -2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(6.0F, KeyframeAnimations.posVec(0.0F, 4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("InnerLeg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("InnerLeg2", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(1.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.posVec(0.0F, -4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.posVec(0.0F, -7.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Feet2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("InnerLeg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("InnerLeg3", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(1.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.posVec(0.0F, -4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.posVec(0.0F, -7.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Feet3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Leg4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.0F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.degreeVec(22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("InnerLeg4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("InnerLeg4", new AnimationChannel(AnimationChannel.Targets.POSITION,
				new Keyframe(1.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.posVec(0.0F, -4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.posVec(0.0F, -7.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.addAnimation("Feet4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
				new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(2.5F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
				new Keyframe(3.5F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();
}