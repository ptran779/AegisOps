package com.github.ptran779.aegisops.client;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public class RecursiveAnimationHelper {
  public static void animate(BoneHierachy model, AnimationDefinition animDef, float timeSeconds, float scale, Vector3f scratch) {
    model.getRoot().getAllParts().forEach(ModelPart::resetPose);  // clean animation
    recurseAndApply(model.getRootBoneName(), model, animDef.boneAnimations(), timeSeconds, scale, scratch);
  }

  private static void recurseAndApply(String partName, BoneHierachy model, Map<String, List<AnimationChannel>> channels, float timeSeconds, float scale, Vector3f scratch) {
    // compute
    applyAnimation(model.get(partName), channels.get(partName), timeSeconds, scale, scratch);
    List<String> childs = model.getBoneChild(partName);
    if (childs == null) {return;}  // dead_end
    for (String child : childs) {
      recurseAndApply(child, model, channels, timeSeconds, scale, scratch);
    }
  }

  private static void applyAnimation(ModelPart part, List<AnimationChannel> channels, float timeSeconds, float scale, Vector3f scratch) {
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