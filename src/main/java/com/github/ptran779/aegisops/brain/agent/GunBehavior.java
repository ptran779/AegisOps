package com.github.ptran779.aegisops.brain.agent;

import com.github.ptran779.aegisops.brain.api.CombatBehavior;
import com.github.ptran779.aegisops.brain.api.Sensor;
import com.github.ptran779.aegisops.client.animation.AgentLivingAnimation;
import com.github.ptran779.aegisops.client.animation.AnimationLibrary;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class GunBehavior extends CombatBehavior {
  Sensor<Boolean> gunSensor, friendlyLOS;  // should be throttle. one check for gun. the other check for if friendly fire happen
  protected double shootRS;
  protected boolean wasReloading = false;

  public GunBehavior(AbstractAgentEntity agent, double speedScale, int shootR, int dropR, Sensor<Boolean> gunSensor, Sensor<Boolean> friendlyLOS) {
    super(agent, dropR, speedScale);
    this.gunSensor = gunSensor;
    this.friendlyLOS = friendlyLOS;
    this.shootRS = shootR*shootR;
  }

  public void start(){
    super.start();
    agent.setAniMoveStatic(AnimationLibrary.A_LIVING);
    agent.equipGun();
  }

  @Override
  protected boolean useValid() {
    if (target == null || !target.isAlive() || !gunSensor.get()) return false;
    targetRS = agent.distanceToSqr(target);
    return targetRS <= dropRS;
  }

  @Override
  public boolean run() {
    if (!useValid()) return true;
    this.agent.getLookControl().setLookAt(target);
    if (attackCoolDown > 0) attackCoolDown--;

    if (shootRS < targetRS) {
      // nav
      if (!agent.moveto(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue())){return true;};
    } else {
      agent.stopNav();
      if (attackCoolDown == 0) {
        if (wasReloading) {
          agent.setAniMoveStatic(AnimationLibrary.A_LIVING);
        }
        if (friendlyLOS.get()) {
          resetCooldown();
          return false;
        }
        wasReloading = agent.shootGun(true);
        resetCooldown();
        if (wasReloading) {
          attackCoolDown += (int) AgentLivingAnimation.RELOAD.lengthInSeconds()*20;
        }
      }
    }
    return false;
  }

  public void stop(){
    super.stop();
    agent.op.aim(false);  // turn off aiming
  }
}
