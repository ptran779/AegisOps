package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.client.model.AbstractAniModel;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
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
  private static final Vector3f scratch_r = new Vector3f();
  private static final Vector3f scratch_p = new Vector3f();
  private static final Vector3f scratch_s = new Vector3f();

  public static void animate(AbstractAniModel model, AnimationDefinition animDef, float timeSeconds, float scale, boolean loop) {
    model.getRoot().getAllParts().forEach(ModelPart::resetPose);  // clean animation
    for (String boneKey : model.getFullBones().keySet()) {
      applyAnimation(model.get(boneKey), animDef.boneAnimations().get(boneKey), loop ? timeSeconds % animDef.lengthInSeconds() : timeSeconds, scale);
    }
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

  // animate to recovery animation... it need the pose position of the current animation to re-interpolate to recovery
  /**
   * Interpolates a humanoid model from a start animation to an end animation.
   *
   * @param model             The agent model to animate.
   * @param startAni          The animation definition that we are transitioning from.
   * @param startAniTimeStamp The current timestamp in seconds for the start animation.
   * @param endAni            The animation definition that we are transitioning to.
   * @param endAniTimeStamp   The current timestamp in seconds for the end animation.
   * @param aniTime           The global animation time used for blending/interpolation.
   * @param tranTime          The total transition duration (in seconds) from start to end pose.
   * @param boneKeys          Map of bone names to ModelPart instances for the humanoid.
   */
  public static void animateHumanoidToPose(
      PlayerModel<AbstractAgentEntity> model, AnimationDefinition startAni, float startAniTimeStamp,
      AnimationDefinition endAni, float endAniTimeStamp, float aniTime, float tranTime, Map<String, ModelPart> boneKeys){

    for (String boneKey : boneKeys.keySet()) {  // each bone key (arm, leg...)
      // compute current pose
      List<AnimationChannel> animBones = startAni.boneAnimations().get(boneKey);
      if (animBones != null) {
        for (AnimationChannel channel : animBones ) {  // each chanel (rot, tran)
          Keyframe[] keyframes = channel.keyframes();
          if (keyframes.length == 0) continue;

          int i = Math.max(0, Mth.binarySearch(0, keyframes.length, k -> startAniTimeStamp <= keyframes[k].timestamp()) - 1);
          int j = Math.min(keyframes.length - 1, i + 1);
          Keyframe from = keyframes[i];
          Keyframe to = keyframes[j];

          float t = (j != i) ? Mth.clamp((startAniTimeStamp - from.timestamp()) / (to.timestamp() - from.timestamp()), 0.0F, 1.0F) : 0.0F;

          if (channel.target() == AnimationChannel.Targets.POSITION){
            to.interpolation().apply(scratch_p, t, keyframes, i, j, 1);
          } else if (channel.target() == AnimationChannel.Targets.ROTATION) {
            to.interpolation().apply(scratch_r, t, keyframes, i, j, 1);
          } else if (channel.target() == AnimationChannel.Targets.SCALE) {
            to.interpolation().apply(scratch_s, t, keyframes, i, j, 1);
          }
        }
      }
      // compute translational to recover pose
      List<AnimationChannel> animBones2 = endAni.boneAnimations().get(boneKey);
      if (animBones2 != null) {
        for (AnimationChannel channel : animBones2) {
          Keyframe[] keyframes = channel.keyframes();
          if (keyframes.length == 0) continue;

          int i = Math.max(0, Mth.binarySearch(0, keyframes.length, k -> endAniTimeStamp <= keyframes[k].timestamp()) - 1);
          int j = Math.min(keyframes.length - 1, i + 1);
          Keyframe from = keyframes[i];
          Keyframe to = keyframes[j];
          float t = (j != i) ? Mth.clamp((endAniTimeStamp - from.timestamp()) / (to.timestamp() - from.timestamp()), 0.0F, 1.0F) : 0.0F;
          Vector3f endPose = scratch; // temporary
          to.interpolation().apply(endPose, t, keyframes, i, j, 1);


          float tGlob = Mth.clamp(aniTime / tranTime, 0.0F, 1.0F);
          if (channel.target() == AnimationChannel.Targets.POSITION) {
            scratch_p.x = Mth.lerp(tGlob, scratch_p.x(), endPose.x());
            scratch_p.y = Mth.lerp(tGlob, scratch_p.y(), endPose.y());
            scratch_p.z = Mth.lerp(tGlob, scratch_p.z(), endPose.z());
          } else if (channel.target() == AnimationChannel.Targets.ROTATION) {
            scratch_r.x = Mth.lerp(tGlob, scratch_r.x(), endPose.x());
            scratch_r.y = Mth.lerp(tGlob, scratch_r.y(), endPose.y());
            scratch_r.z = Mth.lerp(tGlob, scratch_r.z(), endPose.z());
          } else if (channel.target() == AnimationChannel.Targets.SCALE) {
            scratch_s.x = Mth.lerp(tGlob, scratch_s.x, endPose.x());
            scratch_s.y = Mth.lerp(tGlob, scratch_s.y, endPose.y());
            scratch_s.z = Mth.lerp(tGlob, scratch_s.z, endPose.z());
          }
        }
      }
      //apply all transformation stack
      AnimationChannel.Targets.POSITION.apply(boneKeys.get(boneKey), scratch_p);
      AnimationChannel.Targets.ROTATION.apply(boneKeys.get(boneKey), scratch_r);
      AnimationChannel.Targets.SCALE.apply(boneKeys.get(boneKey), scratch_s);
    }
    // apply external layer on main body
    model.leftSleeve.copyFrom(model.leftArm);
    model.rightSleeve.copyFrom(model.rightArm);
    model.leftPants.copyFrom(model.leftLeg);
    model.rightPants.copyFrom(model.rightLeg);
    model.jacket.copyFrom(model.body);
    model.hat.copyFrom(model.head);
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