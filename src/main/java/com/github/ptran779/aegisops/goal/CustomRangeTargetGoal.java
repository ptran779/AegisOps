package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.util.IEntityTarget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class CustomRangeTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
  private double scanRange;
  IEntityTarget modEntity;

  public CustomRangeTargetGoal(IEntityTarget modEntity, Class<T> pTargetType, int pRandomInterval, double scanRange, boolean pMustSee, Predicate pTargetPredicate) {
    super((Mob) modEntity, pTargetType, pRandomInterval, pMustSee, false, pTargetPredicate);
    this.scanRange = scanRange;

    this.targetConditions.range(scanRange);
    this.modEntity = modEntity;
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
    if (!modEntity.haveWeapon()) {return false;}
    if (modEntity.getTarget() != null && modEntity.getTarget().isAlive()) {return false;}  // don't acquire target if already have one. combat will decide if target too far away
    if (modEntity.getTargetMode() == Utils.TargetMode.OFF || this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {return false;}
    else {
      this.findTarget();
      return this.target != null;
    }
  }

  public boolean canContinueToUse() {return false;}

  public void start() {
    this.mob.setTarget(this.target);
    super.start();
  }

  public void stop() {}
}
