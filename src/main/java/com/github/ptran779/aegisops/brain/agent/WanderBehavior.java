package com.github.ptran779.aegisops.brain.agent;

import com.github.ptran779.aegisops.brain.api.ThrottleBehavior;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class WanderBehavior extends ThrottleBehavior {
  protected final AbstractAgentEntity agent;
  Vec3 towards;

  public WanderBehavior(AbstractAgentEntity agent, int cooldown) {
    super(cooldown, 0, agent);
    this.agent = agent;
  }

  @Override
  public boolean canUse() {
    return super.canUse() && agent.getMovement()==0;
  }

  public void start(){
    towards = DefaultRandomPos.getPos(agent, 10, 7);
  }

  @Override
  public boolean run() {
    if (towards == null || agent.getMovement() != 0) {return true;}
    return !agent.moveto(towards, 0.4);
  }
}
