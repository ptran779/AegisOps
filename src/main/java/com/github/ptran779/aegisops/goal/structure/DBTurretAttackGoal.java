package com.github.ptran779.aegisops.goal.structure;

import com.github.ptran779.aegisops.entity.structure.DBTurret;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class DBTurretAttackGoal extends Goal {
  private final DBTurret turret;
  private final double maxRangeSq;
  private final int attackPeriod;
  private int attackCoolDown = 0;

  private int seeTime = 0;
  private double targetDistSq;

  /// fixme later with the changing of targeting goal
  public DBTurretAttackGoal(DBTurret turret, double maxRange, int attackPeriod) {
    this.setFlags(EnumSet.of(Flag.LOOK));

    this.turret = turret;
    this.maxRangeSq = maxRange*maxRange;
    this.attackPeriod = attackPeriod;
  }

  public boolean canUse() {
    return turret.getTarget() != null && turret.getTarget().isAlive() && WandECheck();
  }

  public boolean canContinueToUse() {
    return canUse() && seeTime >= -100;
  }

  private boolean WandECheck(){
    // ammo check here too
    targetDistSq = turret.distanceToSqr(turret.getTarget());
    return targetDistSq < maxRangeSq;
  }

  public void start() {
    turret.setAggressive(true);
    seeTime = 0;
    attackCoolDown = 0;
  }
  public void stop() {
    turret.setAggressive(false);
    turret.setTarget(null); // should already clear but just in case
  }

  public boolean requiresUpdateEveryTick() {return true;}

  public void tick() {
    LivingEntity target = turret.getTarget();
    // if I clear target, it shit itself...
    if (target == null) {return;}
    // Normal targeting logic
    turret.getLookControl().setLookAt(target);
    if (turret.tickCount % 5 == 0) {
      if (turret.getSensing().hasLineOfSight(target)) {
        seeTime = Math.min(20, seeTime + 5); // accelerate buildup
      } else {
        seeTime = seeTime - 5; // don't go wild negative
      }
    }
    // shoot the freak
    if (this.attackCoolDown > 0) this.attackCoolDown--;
    if(this.attackCoolDown <=0 && this.seeTime == 20) {
      turret.shoot();
      attackCoolDown = attackPeriod;
    }
  }
}
