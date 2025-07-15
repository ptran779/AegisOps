package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.UUID;

public class CustomRetaliationTargetGoal extends AbstractThrottleGoal {
  private LivingEntity nextTarget = null;
  AbstractAgentEntity agent;
  private static final short t_count_max = 10;
  private short t_count = 0;

  public CustomRetaliationTargetGoal(AbstractAgentEntity agent) {
    super(agent, t_count_max);
    this.agent = agent;
    this.setFlags(EnumSet.of(Flag.TARGET));
  }

  @Override
  public boolean canUse() {
    if (!super.canUse() || agent.getTarget() != null) {return false;}
    resetThrottle();
    // Check boss if available
    UUID bossUUID = agent.getBossUUID();
    if (bossUUID == null) {return false;}
    ServerPlayer boss = agent.level().getServer().getPlayerList().getPlayer(bossUUID);
    if (boss != null) {
      LivingEntity bossAttacker = boss.getLastHurtByMob();
      if (bossAttacker != null && bossAttacker.isAlive() && !agent.sameTeam(bossAttacker)) {
        agent.setTarget(bossAttacker);
        return true;
      }
    }
    return false;
  }

  public boolean canContinueToUse() {return false;}
  @Override
  public void start() {
    if (nextTarget != null) {
      agent.setTarget(nextTarget);
      nextTarget = null;
    }
  }
}
