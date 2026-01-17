package com.github.ptran779.aegisops.brain.api;

import com.github.ptran779.aegisops.attribute.AgentAttribute;
import com.github.ptran779.aegisops.client.animation.AnimationLibrary;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.world.entity.LivingEntity;

// mostly support timing executing, cooldown method, and all of its thing
public abstract class CombatBehavior extends Behavior {
  protected AbstractAgentEntity agent;
  protected LivingEntity target;
  protected int attackCoolDown;
  protected double dropRS, targetRS;
  protected double speedScale;

  public CombatBehavior(AbstractAgentEntity agent, int dropR, double speedScale) {
    this.agent = agent;
    this.dropRS = dropR*dropR;
    this.speedScale = speedScale;
  }

  protected void resetCooldown() {
    attackCoolDown = (int) Math.max(1, (20 / (agent.getAttribute(AgentAttribute.AGENT_ATTACK_SPEED).getValue() * speedScale)));
  }

  public void start(){
    agent.setAggressive(true);
    resetCooldown();
  }

  public boolean canUse() {
    target = agent.getTarget();
    return useValid();
  }

  abstract protected boolean useValid();  // fast imp since can use call check and assign target already

  public void stop(){
    agent.setAggressive(false);
    agent.stopNav();
    agent.setAniMoveStatic(AnimationLibrary.A_LIVING);
  }
}
