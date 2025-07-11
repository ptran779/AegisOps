package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.attribute.AgentAttribute;
import com.github.ptran779.aegisops.client.animation.AgentAnimation;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.github.ptran779.aegisops.network.AgentRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

public class AgentAttackGoal extends Goal {
  protected final AbstractAgentEntity agent;
  protected int attackCoolDown = 0;
  protected int seeTime=0;
  protected final double meleeRangeSq;       //use melee
  protected final double gunLowRangeSq;      //lower bound chasing gun range
  protected final double gunHighRangeSq;     //upper bound chasing gun range
  protected final double maxRangeSq;         //abandon if too far
  IGunOperator op;
  protected int strikeTick = -1;
  protected boolean meleeYes = false;
  protected boolean gunYes = false;
  double targetDistSq = 0;

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

  protected void shootGun(boolean precision){
    // check for friendly on line. else dont shoot and just move to cooldown
    if (Utils.hasFriendlyInLineOfFire(agent, agent.getTarget())) {
      attackCoolDown = computeAttackCooldown();  // we're not in the clear, do not cause friendly fire
      return;
    }

    prepAttack();
    agent.setYRot(agent.getYHeadRot());
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
        // ANI
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new AgentRenderPacket(agent.getId(), 1));
        agent.setAniMove(Utils.AniMove.RELOAD);
        agent.level().playSound(null, agent, SoundEvents.SLIME_SQUISH, SoundSource.BLOCKS, 1.2f, 0.5f);
        attackCoolDown =  computeAttackCooldown() + ((int) AgentAnimation.RELOAD.lengthInSeconds())*20;  // reload should take time
      }
    }
  }

  protected void reloadGun(){
    int reloadAmount = 0;

    ItemStack gunStack = agent.getMainHandItem();
    AbstractGunItem gunItem = (AbstractGunItem)gunStack.getItem();
    ResourceLocation gunResource = gunItem.getGunId(gunStack);
    CommonGunIndex gunIndex = TimelessAPI.getCommonGunIndex(gunResource).orElse(null);
    int maxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(gunStack, gunIndex.getGunData());
    int curAmmoCount = agent.inventory.checkGunAmmo(gunStack, gunItem);

    // if use virtual ammo
    int virtAmmo = agent.getVirtualAmmo();
    if (virtAmmo > 0){
      reloadAmount = Math.min(virtAmmo, maxAmmoCount - curAmmoCount);
      agent.setVirtualAmmo(virtAmmo - reloadAmount);
    } else {
      // find ammo
      int i = agent.inventory.findGunAmmo(gunStack);
      if (i == -1) return;
      // compute amount
      ItemStack ammoStack = agent.inventory.getItem(i);
      if (ammoStack.getItem() instanceof IAmmoBox iAmmoBoxItem) {
        reloadAmount = Math.min(maxAmmoCount - curAmmoCount, iAmmoBoxItem.getAmmoCount(ammoStack));
        iAmmoBoxItem.setAmmoCount(ammoStack,iAmmoBoxItem.getAmmoCount(ammoStack)-reloadAmount);
      } else if(ammoStack.getItem() instanceof IAmmo){
        reloadAmount = Math.min(maxAmmoCount - curAmmoCount, ammoStack.getCount());
        ammoStack.setCount(ammoStack.getCount() - reloadAmount);
      }
    }
    gunItem.setCurrentAmmoCount(gunStack,curAmmoCount+reloadAmount);
  }

  protected double getAttackReachSqr(LivingEntity target) {return Math.pow((agent.getBbWidth() + target.getBbWidth())/2+2, 2);}

  protected void prepAttack(){  // rotate the body identical to head to avoid award calculation
    float snapYaw = agent.getYHeadRot();
    agent.setYRot(snapYaw);
    agent.setYBodyRot(snapYaw);
  }

  public boolean canUse() {return this.agent.getTarget() != null && !agent.isUsingItem() && this.agent.getTarget().isAlive() && WandECheck();}

  public boolean canContinueToUse() {return canUse() && seeTime >= -100;}

  private boolean WandECheck(){
    // just some extra check + update
    meleeYes = agent.inventory.meleeExist();
    gunYes = agent.inventory.gunExistWithAmmo();
    return meleeYes || gunYes;
  }

  public void start() {
    this.agent.setAggressive(true);
    this.seeTime = 0;
    this.attackCoolDown = 0;
  }

  public void stop() {
    agent.setAniMove(Utils.AniMove.NORM);
    this.agent.setAggressive(false);
    agent.stopNav();
    op.aim(false);  // turn off aiming
    this.agent.stopUsingItem();  // not sure if i need it, but why not
  }

  public boolean requiresUpdateEveryTick() {return true;}

  public void tick() {
    LivingEntity target = agent.getTarget();
    // if I clear target, it shit itself...
    if (target == null) {return;}
    targetDistSq = agent.distanceToSqr(agent.getTarget());
    if (targetDistSq > maxRangeSq) {
      agent.clearTarget();
      return;
    }

    // strike delay handling
    if (strikeTick > 0){
      if (--strikeTick == 10){
        --strikeTick;  // set to -1, meaning no strike in query
        // forward motion -- cause it make sense
        Vec3 look = agent.getLookAngle();
        double dashSpeed = 0.5; // tune to taste, maybe 0.1–0.2
        agent.setDeltaMovement(look.x * dashSpeed, agent.getDeltaMovement().y, look.z * dashSpeed);
        /// WIP more strike ani + logic
        if (targetDistSq < getAttackReachSqr(target)) {
          agent.doHurtTarget(target);
          agent.getMainHandItem().hurtAndBreak(1, agent, (e) -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
      } else if (strikeTick <= 0){
        agent.setAniMove(Utils.AniMove.NORM);
        attackCoolDown = computeAttackCooldown();
      };
      return;
    }

    // Normal targeting logic
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
      agent.setAniMove(Utils.AniMove.NORM);
      // melee prioritize
      if (meleeYes && (targetDistSq < meleeRangeSq || !gunYes && targetDistSq < meleeRangeSq*4)) { // close quarter
        agent.equipMelee();
        if (getAttackReachSqr(target) + 2 > targetDistSq) { // hit offset due to forward lunge ///WIP
          prepAttack();
          agent.stopNav();
          strikeTick = 15;
          PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new AgentRenderPacket(agent.getId(), 1));
          agent.setAniMove(Utils.AniMove.ATTACK);
        } else {
          if(!agent.moveto(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue())) agent.setTarget(null);
        }
      }

      else if (gunYes) {
        agent.equipGun();
        if (targetDistSq > gunHighRangeSq) { // too far, move closer
          agent.moveto(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
        } else if (targetDistSq > gunLowRangeSq) { // snipe that
          agent.stopNav();
          shootGun(true);
        } else if (targetDistSq > meleeRangeSq || !meleeYes) { // too close , move further, or someone has no melee :(
          agent.getMoveControl().strafe(-0.5F, 0F);
          shootGun(false);
        }
      } else {// we have no weapon :)
        agent.clearTarget();  //trigger stop this goal
      }
    }
  }
}
