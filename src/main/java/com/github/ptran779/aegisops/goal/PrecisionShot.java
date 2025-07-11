package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.github.ptran779.aegisops.network.AgentRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

public class PrecisionShot extends AgentAttackGoal {
  int seeTime = 0;
  final int coolDown;
  int coolDownTimer = 0;
  int tickAction = -1;

  public PrecisionShot(AbstractAgentEntity agent, int maxRange, int coolDown) {
    super(agent, 0, 0, maxRange, maxRange);
    this.coolDown = coolDown;
  }

  @Override
  public boolean canUse() {
    if (!agent.getAllowSpecial()) return false;
    if (coolDownTimer > 0) {coolDownTimer--;}
    return coolDownTimer == 0 && agent.getTarget() != null && this.agent.getTarget().isAlive() &&
        agent.inventory.gunExistWithAmmo();
  }
  public boolean canContinueToUse() {
    return coolDownTimer == 0 && agent.getTarget() != null && this.agent.getTarget().isAlive() &&
      agent.inventory.gunExistWithAmmo() && seeTime >= -100;
  }
  public void start() {
    agent.setAggressive(true);
    seeTime = 0;
    tickAction = -1;
    agent.equipGun();
    reloadGun();
    op.aim(true);
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
        agent.setAniMove(Utils.AniMove.SPECIAL0);
        tickAction = agent.tickCount;
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new AgentRenderPacket(agent.getId(), 1));
      } else if (dummy == 60) {
        ItemStack stack = agent.getMainHandItem();
        CommonGunIndex gunIndex = TimelessAPI.getCommonGunIndex(((AbstractGunItem) stack.getItem()).getGunId(stack)).orElse(null);
        if (gunIndex != null) {
          float baseDamage = (float) AttachmentDataUtils.getDamageWithAttachment(stack, gunIndex.getGunData());
          target.hurt(agent.level().damageSources().mobAttack(agent), baseDamage*2);  // I dont have a way to modify bullet damage :)
          shootGun(true);
        }
        coolDownTimer = coolDown;
      }
    }
  }
}
