package com.github.ptran779.aegisops.client.animation;

import net.minecraft.client.animation.AnimationDefinition;

import java.util.ArrayList;
import java.util.List;

public class AnimationLibrary {
  public static final List<AnimationDefinition> AGENT_ANI = new ArrayList<>();
  // negative registration for composite custom? mostly related to living animation
  public static final int A_LIVING = -1;  // special case for living state

  // normal single animation
  public static final int A_IDLE = reg(AgentLivingAnimation.IDLE);
  public static final int A_WALK = reg(AgentLivingAnimation.WALK);
  public static final int A_RUN = reg(AgentLivingAnimation.RUN);
  public static final int A_RELOAD = reg(AgentLivingAnimation.RELOAD);

  public static final int A_EATING = reg(AgentLivingAnimation.EATING);
  public static final int A_SALUTE = reg(AgentLivingAnimation.SALUTE);
  public static final int A_TRIPLE_STRIKE = reg(AgentLivingAnimation.TRIPLE_STRIKE);

  public static final int A_BONK = reg(AgentLivingAnimation.BONK);
;
    private static int reg(AnimationDefinition def) {
      AGENT_ANI.add(def);
        return AGENT_ANI.size() - 1;
    }

    public static AnimationDefinition get(int id) {
        return AGENT_ANI.get(id);
    }
}
