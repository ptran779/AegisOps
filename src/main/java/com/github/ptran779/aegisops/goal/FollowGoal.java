package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FollowGoal extends Goal {
  AbstractAgentEntity agent;
  LivingEntity followTarget;

  public FollowGoal(AbstractAgentEntity agent) {
    this.agent = agent;
    this.setFlags(EnumSet.of(Flag.MOVE));
  }

  @Override
  public boolean canUse() {
    if(!(agent.getMovement() == 2) || agent.followPlayer == null) return false;
    this.followTarget = ((ServerLevel) agent.level()).getServer().getPlayerList().getPlayer(agent.followPlayer);
    if (followTarget == null) return false;
    double dist = agent.distanceToSqr(followTarget);
    return 32*32 > dist && dist > 6*6;
  }

  public boolean canContinueToUse() {
    double dist = agent.distanceToSqr(followTarget);
    return agent.getMovement() == 2 && 32*32 > dist && dist > 3*3;
  }

  @Override
  public void tick() {
    if (agent.getBossUUID() != null) {
      agent.moveto(followTarget, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
    }
  }

  public void stop(){
    agent.stopNav();
  }
}
