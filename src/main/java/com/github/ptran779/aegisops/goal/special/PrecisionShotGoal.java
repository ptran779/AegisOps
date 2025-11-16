package com.github.ptran779.aegisops.goal.special;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.goal.AbstractThrottleGoal;
import com.github.ptran779.aegisops.network.render.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

public class PrecisionShotGoal extends AbstractThrottleGoal {
  int seeTime = 0;
  int tickAction = -1;
  AbstractAgentEntity agent;

  public PrecisionShotGoal(AbstractAgentEntity agent, int coolDown) {
    super(agent, coolDown);
    this.agent = agent;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
  }
  public boolean canUse() {
    if (!agent.getAllowSpecial()) return false;
    return super.canUse() && agent.getTarget() != null && this.agent.getTarget().isAlive() && agent.inventory.gunExistWithAmmo();
  }
  public boolean canContinueToUse() {
    return canUse() && seeTime >= -100;
  }
  public void start() {
    agent.setAggressive(true);
    seeTime = 0;
    tickAction = -1;
    agent.equipGun();
    agent.reloadGun();
    agent.op.aim(true);
  }

  public void stop() {
    agent.setAniMove(Utils.AniMove.NORM);
    this.agent.setAggressive(false);
    agent.stopNav();
    agent.op.aim(false);  // turn off aiming
    agent.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    this.agent.stopUsingItem();  // not sure if i need it, but why not
  }

  public boolean isInterruptable(){return false;}

  public void tick() {
    LivingEntity target = agent.getTarget();
    if (target == null) {return;}
    // Normal targeting logic
    this.agent.getLookControl().setLookAt(target);
    if (agent.tickCount % 5 == 0) {
      if (agent.getSensing().hasLineOfSight(target)) {
        seeTime = Math.min(20, seeTime + 5); // accelerate buildup
      } else {
        seeTime = seeTime - 5; // don't go wild negative
      }
    }

    if(this.seeTime == 20) {
      int dummy = agent.tickCount-tickAction;
      if (tickAction == -1) {
        agent.setSpecialMove(0);
        tickAction = agent.tickCount;
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
      } else if (dummy == 60) {
        ItemStack stack = agent.getMainHandItem();
        CommonGunIndex gunIndex = TimelessAPI.getCommonGunIndex(((AbstractGunItem) stack.getItem()).getGunId(stack)).orElse(null);
        if (gunIndex != null) {
          float baseDamage = (float) AttachmentDataUtils.getDamageWithAttachment(stack, gunIndex.getGunData());
          target.hurt(agent.level().damageSources().mobAttack(agent), baseDamage*2);  // I dont have a way to modify bullet damage :)
          agent.shootGun(true);
          resetThrottle();  // reset cooldown and trigger stop goal
        }
      }
    }
  }
}
