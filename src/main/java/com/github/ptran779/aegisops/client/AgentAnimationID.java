package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.client.animation.AgentLivingAnimation;
import net.minecraft.client.animation.AnimationDefinition;

public final class AgentAnimationID {
  public static final int IDLE = 0;
  public static final int WALK = 1;
  public static final int RUN = 2;
  public static final int RECOVER_IDLE = 21;

//  // special
//  public static final int SPECIAL_HEAL = 100;
//  public static final int SPECIAL_OVERDRIVE = 101;

  // map to actual animation
  public static final AnimationDefinition[] ANIMATIONS = new AnimationDefinition[100];
  static {
    ANIMATIONS[IDLE] = AgentLivingAnimation.IDLE;
    ANIMATIONS[WALK] = AgentLivingAnimation.WALK;
    ANIMATIONS[RUN] = AgentLivingAnimation.RUN;
  }
}