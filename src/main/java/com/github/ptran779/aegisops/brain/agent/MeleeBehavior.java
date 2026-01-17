package com.github.ptran779.aegisops.brain.agent;

import com.github.ptran779.aegisops.brain.api.CombatBehavior;
import com.github.ptran779.aegisops.brain.api.Sensor;
import com.github.ptran779.aegisops.client.animation.AnimationLibrary;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.network.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class MeleeBehavior extends CombatBehavior {
  Sensor<ItemStack> meleeIS;
  double meleeRS;
  private int sCount = -1;
  private boolean striking = false;
  private final int recoverTick = 20; // transition time back to idle  fixme will work on a universal smooth transition later

  private int dummy;

  protected double getAttackReachSqr(LivingEntity target) {return Math.pow((agent.getBbWidth() + target.getBbWidth())/2+2, 2);}

  public MeleeBehavior(AbstractAgentEntity agent, int meleeR, int dropR, Sensor<ItemStack> meleeIS) {
    super(agent, meleeR, dropR);
    this.meleeIS = meleeIS;
    this.meleeRS = meleeR*meleeR;
  }

  protected void fixRotation(){  // rotate the body identical to head to avoid award calculation
    float snapYaw = agent.getYHeadRot();
    agent.setYRot(snapYaw);
    agent.setYBodyRot(snapYaw);
  }

  protected boolean useValid(){
    if (target == null || !target.isAlive() || meleeIS.get().isEmpty()) return false;
    targetRS = agent.distanceToSqr(target);
    return targetRS <= dropRS;
  }

  public void start() {
    super.start();
    agent.setAniMoveStatic(AnimationLibrary.A_LIVING);
    PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent), new EntityRenderPacket(agent.getId(), 1));
    striking = false;
    agent.equipMelee();
    sCount = -1;
  }

  @Override
  public boolean run() {
    if (!useValid()) return true;

    this.agent.getLookControl().setLookAt(target);
    if (attackCoolDown > 0) attackCoolDown--;

    // approach target if far
    if (!striking) {
      if (targetRS > meleeRS){
        if (!agent.moveto(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue())) return true; // cant reach target
      } else {
        striking = true;
      }
    } else if (attackCoolDown == 0){
      if (sCount == -1) {
        agent.stopNav();
        dummy = agent.tickCount;
        agent.setAniMoveStatic(AnimationLibrary.A_TRIPLE_STRIKE);
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent), new EntityRenderPacket(agent.getId(), 1));
        sCount = 0;
      } else {
        fixRotation();
        int tProg = agent.tickCount - dummy;

        if (tProg == 8 + sCount * 20) {
          Vec3 look = agent.getLookAngle();
          double dashSpeed = 0.5; // tune to taste, maybe 0.1–0.2
          agent.setDeltaMovement(look.x * dashSpeed, agent.getDeltaMovement().y, look.z * dashSpeed);
        } else if (tProg == 10 + sCount * 20) {
//          System.out.println("hitting "+sCount);
          if (targetRS < getAttackReachSqr(target)) {
            agent.doHurtTarget(target);
            agent.getMainHandItem().hurtAndBreak(1, agent, (e) -> e.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            if (sCount < 2) sCount++;  // leave
            else {
              agent.setAniMoveTransition(AnimationLibrary.A_TRIPLE_STRIKE, AnimationLibrary.A_IDLE, tProg / 20f, 0, recoverTick / 20f); // tran back to pose
              PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent), new EntityRenderPacket(agent.getId(), 1));
            }
          } else {
            agent.setAniMoveTransition(AnimationLibrary.A_TRIPLE_STRIKE, AnimationLibrary.A_IDLE, tProg / 20f, 0, recoverTick / 20f); // tran back to pose
            PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent), new EntityRenderPacket(agent.getId(), 1));
          }
        } else if (tProg > 10 + sCount * 20 + recoverTick) {
          resetCooldown();
          striking = false;
          sCount = -1;
        }
      }
    }
    return false;
  }
}
