package com.github.ptran779.aegisops.goal.special;

import com.github.ptran779.aegisops.entity.extra.VectorPursuer;
import com.github.ptran779.aegisops.server.ItemInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED;

public class DronePursudeGoal extends Goal {
  VectorPursuer vp;
  LivingEntity tPursude;
  LivingEntity tBoss;
  boolean lockOn = false;

  public DronePursudeGoal(VectorPursuer vp) {
    this.vp = vp;
  }

  public boolean canUse() {
    return vp.getDeployed();
  }
  public void start() {
    if (vp.getTarget() != null && vp.getTarget().isAlive()) {tPursude = vp.getTarget();}
    if (vp.deployerUUID != null) {
      Entity e = ((ServerLevel)vp.level()).getEntity(vp.deployerUUID);
      if (e instanceof LivingEntity livingEntity) {tBoss = livingEntity;}
    }
  }

  public boolean requiresUpdateEveryTick() {return true;}
  public void tick() {
    if (tPursude != null && tPursude.isAlive()) {
      double distSqr = vp.distanceToSqr(tPursude);
      if (!lockOn && distSqr < 144 && vp.getSensing().hasLineOfSight(tPursude)) {lockOn = true;}
      vp.getLookControl().setLookAt(tPursude, 30.0F, 30.0F);
      if (lockOn && vp.tickCount % 4 == 0) {
        Vec3 viewVec = vp.getLookAngle(); // already normalized
        vp.setDeltaMovement(viewVec.scale(0.75)); // adjust speed
        if (distSqr <= 2) {
          vp.level().explode(vp, vp.getX(), vp.getY(), vp.getZ(), 4.0f, Level.ExplosionInteraction.NONE );
          vp.discard();
        }
      } else {
        vp.getNavigation().moveTo(tPursude, vp.getAttributeValue(FLYING_SPEED));
      }
    } else if (tBoss != null) {
      if (vp.distanceToSqr(tBoss) <= 2) {dropItems();}
      vp.getNavigation().moveTo(tBoss, vp.getAttributeValue(FLYING_SPEED));
    } else {dropItems();}
  }

  protected void dropItems() {
    ItemStack drop = new ItemStack(ItemInit.VP_ITEM.get());
    ItemEntity itemEntity = new ItemEntity(vp.level(),vp.getX(),vp.getY(),vp.getZ(),drop);
    vp.level().addFreshEntity(itemEntity);
    vp.discard();
  }
}