package com.github.ptran779.aegisops.brain.agent;

import com.github.ptran779.aegisops.brain.api.Behavior;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FollowBehavior extends Behavior {
  AbstractAgentEntity agent;
  LivingEntity followTarget;
  int maxDistance;
  int minDistance;
  int targetDistance;


  public FollowBehavior(AbstractAgentEntity agent, int maxDistance, int minDistance, int targetDistance) {
    this.agent = agent;
    this.maxDistance = maxDistance;
    this.minDistance = minDistance;
    this.targetDistance = targetDistance;
  }

  @Override
  public boolean canUse() {
    if(!(agent.getMovement() == 2) || agent.followPlayer == null) return false;
    this.followTarget = ((ServerLevel) agent.level()).getServer().getPlayerList().getPlayer(agent.followPlayer);
    if (followTarget == null) return false;
    double dist = agent.distanceToSqr(followTarget);
    return maxDistance*maxDistance > dist && dist > minDistance*minDistance;
  }

  @Override
  public boolean run() {
    if ((agent.getMovement() == 2) && followTarget != null && followTarget.isAlive()) {
      double dist = agent.distanceToSqr(followTarget);
      if (maxDistance*maxDistance > dist && dist > targetDistance*targetDistance) {
        agent.moveto(followTarget, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
        return false;
      }
      return true;
    }
    return true;
  }

  public void stop(){
    agent.stopNav();
  }
}
