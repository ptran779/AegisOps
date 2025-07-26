package com.github.ptran779.aegisops.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class AbstractThrottleGoal extends Goal {
  protected int checkInterval;
  private int checkTime=0;
  LivingEntity user;

  protected AbstractThrottleGoal(LivingEntity user, int checkInterval) {
    this.user = user;
    this.checkInterval = checkInterval;
  }

  public void resetThrottle() {
    checkTime = user.tickCount;
  }

  public boolean canUse() {
    return user.tickCount - checkTime >= checkInterval;
  }
}
