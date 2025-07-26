package com.github.ptran779.aegisops.goal.common;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.api.IEntityTarget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class CustomRangeTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
  protected double scanRange;
  protected double dropRangeSq;
  IEntityTarget modEntityTarget;
  protected int seeTime = 0;
  protected int losCheckCooldown = 0;

  public CustomRangeTargetGoal(IEntityTarget modEntity, Class<T> pTargetType, int pRandomInterval, double scanRange, double dropRange, boolean pMustSee, Predicate pTargetPredicate) {
    super((Mob) modEntity, pTargetType, pRandomInterval, pMustSee, false, pTargetPredicate);
    this.scanRange = scanRange;
    this.dropRangeSq = dropRange * dropRange;
    this.targetConditions.range(scanRange);
    this.modEntityTarget = modEntity;
  }

  protected void findTarget() {
    if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
      this.target = this.mob.level().getNearestEntity(
          this.mob.level().getEntitiesOfClass(
              this.targetType,
              this.getTargetSearchArea(this.scanRange),
              (p_148152_) -> true),
          this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    } else {
      this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }
  }

  public boolean canUse() {
    if (mob.getTarget() != null && mob.getTarget().isAlive()) {return false;}
    if (modEntityTarget.getTargetMode() == Utils.TargetMode.OFF || this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {return false;}
    if (!modEntityTarget.haveWeapon()) {return false;}
    else {
      this.findTarget();
      return this.target != null;
    }
  }

  public boolean canContinueToUse() {
    if (mob.getTarget() == null ||
        !mob.getTarget().isAlive() ||
        mob.distanceToSqr(mob.getTarget()) > this.dropRangeSq ||
        !modEntityTarget.haveWeapon()) {return false;}
    if (--losCheckCooldown <= 0) {
      losCheckCooldown = 5;
      if (mob.getSensing().hasLineOfSight(mob.getTarget())) {
        seeTime = Math.min(10, seeTime + 5);
      } else {
        seeTime -= 5;
      }
    }
    return seeTime > -50;
  }

  public void start() {
    super.start();
    seeTime = 0;
  }

  public void stop() {
    super.stop();
  }
}
