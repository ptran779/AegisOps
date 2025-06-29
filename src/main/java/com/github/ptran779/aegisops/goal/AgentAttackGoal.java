package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.attribute.AgentAttribute;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class AgentAttackGoal extends Goal {
//  private static final int maxActionTime = 300;  // abandon target if stuck? need test with epic fight
  private final AbstractAgentEntity agent;
  private int attackCoolDown = 0;
  private int seeTime=0;
  private final double meleeRangeSq;       //use melee
  private final double gunLowRangeSq;      //lower bound chasing gun range
  private final double gunHighRangeSq;     //upper bound chasing gun range
  private final double maxRangeSq;         //abandon if too far
  IGunOperator op;

  public AgentAttackGoal(AbstractAgentEntity agent, double meleeRange, double gunLowRange, double gunHighRange, double maxRange) {
    this.agent = agent;
    this.meleeRangeSq = meleeRange * meleeRange;
    this.gunLowRangeSq = gunLowRange * gunLowRange;
    this.gunHighRangeSq = gunHighRange * gunHighRange;
    this.maxRangeSq = maxRange * maxRange;
    this.op = IGunOperator.fromLivingEntity(agent);

    this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  private int computeAttackCooldown(){return (int) (20.0f/agent.getAttribute(AgentAttribute.AGENT_ATTACK_SPEED).getValue());}

  private void shootGun(boolean precision){
    if (op.getSynIsBolting()) {op.aim(false);}
    if (op.getSynIsAiming() != precision) {
      op.aim(precision);
    }

    switch (op.shoot(() -> agent.getViewXRot(1f), () -> agent.getViewYRot(1f))) {
      case SUCCESS -> {attackCoolDown = computeAttackCooldown();}
      case NOT_DRAW -> {
        op.draw(agent::getMainHandItem);
      }
      case NEED_BOLT -> {
        op.bolt();
        attackCoolDown = computeAttackCooldown();  // mimic bolt action take twice as long
      } case NO_AMMO -> {
        reloadGun();
        attackCoolDown = computeAttackCooldown();  // reload should take time
      }
    }
  }

  private void reloadGun(){
    ItemStack gunStack = agent.getMainHandItem();
    // find ammo
    int i = agent.inventory.findGunAmmo(gunStack);
    if (i == -1) return;
    // compute amount
    ItemStack ammoStack = agent.inventory.getItem(i);
    AbstractGunItem gunItem = (AbstractGunItem)gunStack.getItem();
    ResourceLocation gunResource = gunItem.getGunId(gunStack);
    CommonGunIndex gunIndex = TimelessAPI.getCommonGunIndex(gunResource).orElse(null);

    int maxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(gunStack, gunIndex.getGunData());
    int curAmmoCount = agent.inventory.checkGunAmmo(gunStack, gunItem);

    int reloadAmount = curAmmoCount;
    if (ammoStack.getItem() instanceof IAmmoBox iAmmoBoxItem) {
      reloadAmount = Math.min(maxAmmoCount - curAmmoCount, iAmmoBoxItem.getAmmoCount(ammoStack));
      iAmmoBoxItem.setAmmoCount(ammoStack,iAmmoBoxItem.getAmmoCount(ammoStack)-reloadAmount);
    } else if(ammoStack.getItem() instanceof IAmmo){
      reloadAmount = Math.min(maxAmmoCount - curAmmoCount, ammoStack.getCount());
      ammoStack.setCount(ammoStack.getCount() - reloadAmount);
    }
    gunItem.setCurrentAmmoCount(gunStack,curAmmoCount+reloadAmount);
  }

  protected double getAttackReachSqr(LivingEntity target) {return Math.pow((agent.getBbWidth() + target.getBbWidth())/2+2, 2);}

  public boolean canUse() {
    return this.agent.getTarget() != null && !agent.isUsingItem() && this.agent.getTarget().isAlive();
  }

  public boolean canContinueToUse() {
    return (agent.getTarget() != null && agent.getTarget().isAlive() && seeTime > -100 && agent.distanceToSqr(agent.getTarget()) < maxRangeSq);
  }

  public void start() {
    this.agent.setAggressive(true);
    this.seeTime = 0;
    this.attackCoolDown = 0;

  }

  public void stop() {
    this.agent.setAggressive(false);
    agent.clearTarget(); // should already clear but just in case
    agent.getNavigation().stop();
//    IGunOperator op = IGunOperator.fromLivingEntity(agent);
    op.aim(false);  // turn off aiming
    this.agent.stopUsingItem();  // not sure if i need it, but why not
  }

  public boolean requiresUpdateEveryTick() {return true;}

  public void tick() {
    LivingEntity target = agent.getTarget();
    //if I clear target, it shit itself...
    if (target == null) {return;}
    this.agent.getLookControl().setLookAt(target);
    if (agent.tickCount % 5 == 0) {
      if (agent.getSensing().hasLineOfSight(target)) {
        seeTime = Math.min(20, seeTime + 5); // accelerate buildup
      } else {
        seeTime = seeTime - 5; // don't go wild negative
      }
    }

    // compute attack cooldown
    if (this.attackCoolDown > 0) this.attackCoolDown--;

    if(this.attackCoolDown<=0 && this.seeTime == 20) {
      double targetDistSq = agent.distanceToSqr(target);
      boolean meleeYes = agent.inventory.meleeExist();
      boolean gunYes = agent.inventory.gunExistWithAmmo();
      // melee prioritize
      if (meleeYes && targetDistSq < meleeRangeSq) { // close quarter
        agent.equipMelee();
        if (getAttackReachSqr(target) > targetDistSq) { //FIXME if use epic fight, only handle equip weapon as epic fight will handle attack logic
          agent.getNavigation().stop();
          agent.swing(InteractionHand.MAIN_HAND, true);
          agent.doHurtTarget(target);
          agent.getMainHandItem().hurtAndBreak(1, agent, (e) -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
          attackCoolDown = computeAttackCooldown();
        } else {
          agent.getNavigation().moveTo(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
        }
      } else if (gunYes) {
        agent.equipGun();
        if (targetDistSq > gunHighRangeSq) { // too far, move closer
          agent.getNavigation().moveTo(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
        } else if (targetDistSq > gunLowRangeSq) { // snipe that
          agent.getNavigation().stop();
          shootGun(true);
        } else if (targetDistSq > meleeRangeSq || !meleeYes) { // too close , move further, or someone has no melee :(
          agent.getMoveControl().strafe(-0.5F, 0F);
          shootGun(false);
        }
      } else {// we have no weapon :)
        agent.clearTarget();  //trigger stop this goal
        return;
      }
    }
  }
}
