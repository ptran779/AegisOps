package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public class AnimationHelper {
  private static final Vector3f scratch = new Vector3f();
  public static void animate(IBoneHierachy model, AnimationDefinition animDef, float timeSeconds, float scale) {
    model.getRoot().getAllParts().forEach(ModelPart::resetPose);  // clean animation
    recurseAndApply(model.getRootBoneName(), model, animDef.boneAnimations(), timeSeconds, scale);
  }

  public static void animateHumanoid(PlayerModel<AbstractAgentEntity> model, AnimationDefinition animDef, Map<String, ModelPart> boneKeys, float timeSeconds, float scale, boolean loop){
    // it will loop though all boneKeys, check all animDef for bone with ani, apply modelPart
    // loop key and find animDef
    float aniTime = timeSeconds;
    if (loop) {aniTime = timeSeconds % animDef.lengthInSeconds();}
    for (String boneKey : boneKeys.keySet()) {
      List<AnimationChannel> animBones = animDef.boneAnimations().get(boneKey);
      if (animBones != null) {
        applyAnimation(boneKeys.get(boneKey), animBones, aniTime, scale);
      }
    }
    // apply external layer on main body
    model.leftSleeve.copyFrom(model.leftArm);
    model.rightSleeve.copyFrom(model.rightArm);
    model.leftPants.copyFrom(model.leftLeg);
    model.rightPants.copyFrom(model.rightLeg);
    model.jacket.copyFrom(model.body);
    model.hat.copyFrom(model.head);
  }

  private static void recurseAndApply(String partName, IBoneHierachy model, Map<String, List<AnimationChannel>> channels, float timeSeconds, float scale) {
    // compute
    applyAnimation(model.get(partName), channels.get(partName), timeSeconds, scale);
    List<String> childs = model.getBoneChild(partName);
    if (childs == null) {return;}  // dead_end
    for (String child : childs) {
      recurseAndApply(child, model, channels, timeSeconds, scale);
    }
  }

  private static void applyAnimation(ModelPart part, List<AnimationChannel> channels, float timeSeconds, float scale) {
    if (channels == null) {return;}
    for (AnimationChannel channel : channels) {
      Keyframe[] keyframes = channel.keyframes();
      if (keyframes.length == 0) continue;

      int i = Math.max(0, Mth.binarySearch(0, keyframes.length, k -> timeSeconds <= keyframes[k].timestamp()) - 1);
      int j = Math.min(keyframes.length - 1, i + 1);
      Keyframe from = keyframes[i];
      Keyframe to = keyframes[j];

      float t = (j != i) ? Mth.clamp((timeSeconds - from.timestamp()) / (to.timestamp() - from.timestamp()), 0.0F, 1.0F) : 0.0F;

      to.interpolation().apply(scratch, t, keyframes, i, j, scale);
      channel.target().apply(part, scratch);
    }
  }
}