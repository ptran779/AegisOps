package com.github.ptran779.aegisops.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class AbstractThrottleGoal extends Goal {
  protected int checkInterval;
  private int checkTime=0;
  LivingEntity user;

  AbstractThrottleGoal(LivingEntity user, int checkInterval) {
    this.user = user;
    this.checkInterval = checkInterval;
  }

  public boolean canUse() {
    if (user.tickCount - checkTime < checkInterval) {return false;}
    checkTime = user.tickCount;  // reset counter
    return true;
  }
}
