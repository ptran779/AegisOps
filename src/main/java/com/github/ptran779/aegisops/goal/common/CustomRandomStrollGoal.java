package com.github.ptran779.aegisops.goal.common;

import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

public class CustomRandomStrollGoal extends RandomStrollGoal {
  private final AbstractAgentEntity agent;

  public CustomRandomStrollGoal(AbstractAgentEntity agent, double pSpeedModifier, int cooldownTicks) {
    super(agent, pSpeedModifier, cooldownTicks);
    this.agent = agent;
  }

  @Override
  public boolean canUse() {
    return agent.getMovement() == 0 && super.canUse();
  }

  @Override
  public boolean canContinueToUse() {
    return agent.getMovement() == 0 && super.canContinueToUse();
  }
}
