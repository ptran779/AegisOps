package com.github.ptran779.aegisops.goal.common;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.attribute.AgentAttribute;
import com.github.ptran779.aegisops.client.animation.AgentLivingAnimation;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.network.render.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
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
  protected final double meleeRangeSq;       //use melee
  protected final double gunLowRangeSq;      //lower bound chasing gun range
  protected final double gunHighRangeSq;     //upper bound chasing gun range
  protected int strikeTick = -1;
  protected boolean meleeYes = false;
  protected boolean gunYes = false;
  double targetDistSq = 0;
  protected boolean skip = false;

  public AgentAttackGoal(AbstractAgentEntity agent, double meleeRange, double gunLowRange, double gunHighRange) {
    this.agent = agent;
    this.meleeRangeSq = meleeRange * meleeRange;
    this.gunLowRangeSq = gunLowRange * gunLowRange;
    this.gunHighRangeSq = gunHighRange * gunHighRange;
//    this.maxRangeSq = maxRange * maxRange;
    this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
  }

  private int computeAttackCooldown(){return (int) (20.0f/agent.getAttribute(AgentAttribute.AGENT_ATTACK_SPEED).getValue());}

  protected double getAttackReachSqr(LivingEntity target) {return Math.pow((agent.getBbWidth() + target.getBbWidth())/2+2, 2);}

  protected void prepAttack(){  // rotate the body identical to head to avoid award calculation
    float snapYaw = agent.getYHeadRot();
    agent.setYRot(snapYaw);
    agent.setYBodyRot(snapYaw);
  }

  public boolean canUse() {return !agent.isUsingItem() && this.agent.getTarget() != null && this.agent.getTarget().isAlive() && WandECheck();}

  public boolean canContinueToUse() {
    return !skip && canUse();
  }

  private boolean WandECheck(){
    // just some extra check + update
    meleeYes = agent.inventory.meleeExist();
    gunYes = agent.inventory.gunExistWithAmmo();
    return meleeYes || gunYes;
  }

  public void start() {
    this.agent.setAggressive(true);
    this.attackCoolDown = computeAttackCooldown();
    skip = false;
  }

  public void stop() {
    agent.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    agent.setAniMove(Utils.AniMove.NORM);
    this.agent.setAggressive(false);
    agent.stopNav();
    agent.op.aim(false);  // turn off aiming
    if (agent.getTarget() == null) {agent.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);}  // For smart logic-ish
    this.agent.stopUsingItem();  // not sure if i need it, but why not
  }

  public boolean requiresUpdateEveryTick() {return true;}

  public void tick() {
    LivingEntity target = agent.getTarget();
    // if I clear target, it shit itself...
    if (target == null) {return;}
    this.agent.getLookControl().setLookAt(target);
    targetDistSq = agent.distanceToSqr(agent.getTarget());

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
    // compute attack cooldown
    if (this.attackCoolDown > 0) this.attackCoolDown--;
//    if ((gunYes && gunLowRangeSq > targetDistSq && targetDistSq > meleeRangeSq) || !meleeYes){
//      agent.getMoveControl().strafe(-0.1F, 0F);
//    }
    if(this.attackCoolDown<=0) {
      agent.setAniMove(Utils.AniMove.NORM);
      // melee prioritize
      if (meleeYes && (targetDistSq < meleeRangeSq || !gunYes && targetDistSq < meleeRangeSq*4)) { // close quarter
        agent.equipMelee();
        if (getAttackReachSqr(target) + 2 > targetDistSq) { // hit offset due to forward lunge ///WIP
          prepAttack();
          agent.stopNav();
          strikeTick = 15;
          PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
          agent.setAniMove(Utils.AniMove.ATTACK);
        } else {
          if(!agent.moveto(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue())) skip = true;
        }
      }
      else if (gunYes) {
        agent.equipGun();
        if (targetDistSq > gunHighRangeSq) { // too far, move closer
          agent.moveto(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
        } else if (targetDistSq > gunLowRangeSq) { // snipe that
          agent.stopNav();
          if (agent.shootGun(true)) {
            attackCoolDown =  computeAttackCooldown() + ((int) AgentLivingAnimation.RELOAD.lengthInSeconds())*20;  // reload should take time
          } else {attackCoolDown = computeAttackCooldown();};
        } else if (targetDistSq > meleeRangeSq || !meleeYes) { // too close , move further, or someone has no melee :(
          if (agent.shootGun(false)) {
            attackCoolDown =  computeAttackCooldown() + ((int) AgentLivingAnimation.RELOAD.lengthInSeconds())*20;  // reload should take time
          } else {attackCoolDown = computeAttackCooldown();};
        }
      }
      else {skip = true;}
    }
  }
}
