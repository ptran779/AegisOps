package com.github.ptran779.aegisops.goal.special;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.goal.AbstractThrottleGoal;
import com.github.ptran779.aegisops.item.ModularShieldItem;
import com.github.ptran779.aegisops.network.render.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;
import java.util.List;

public class ShieldBashGoal extends AbstractThrottleGoal {
  AbstractAgentEntity agent;
  protected int tickProgress = -1;
  protected int phase= 0;
  protected final int cooldown;
  protected int lastCooldown;
  LivingEntity target;
  public static final int MAX_CHASE_TIME = 200;

  public ShieldBashGoal(AbstractAgentEntity agent, int checkInterval, int cooldown) {
    super(agent, checkInterval);
    this.agent = agent;
    this.cooldown = cooldown;
    lastCooldown = agent.tickCount;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
  }

  public boolean canUse() {
    if ((agent.tickCount - lastCooldown < cooldown) ||
        !agent.getAllowSpecial() ||
        !(agent.getSpecialSlot().getItem() instanceof ModularShieldItem) ||
        !super.canUse())
    {return false;}

    target = agent.getTarget();
    if (target == null || !target.isAlive()) {return false;};
    Path path = agent.getNavigation().createPath(target, 0);
    resetThrottle();
    return (path != null && path.canReach());
  }

  public boolean canContinueToUse(){
    return (agent.getSpecialSlot().getItem() instanceof ModularShieldItem && phase<=6 && agent.getTarget() != null && agent.getTarget().isAlive());
  }

  public void start() {
    tickProgress = -1;
    phase = 0;
    agent.getSpecialSlot().getOrCreateTag().remove("DeployTick");
  }

  public void stop() {
    lastCooldown = agent.tickCount;  // reset cooldown
    phase = 0;
    agent.setAniMove(Utils.AniMove.NORM);
    agent.getSpecialSlot().getOrCreateTag().remove("DeployTick");
    agent.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    agent.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
  }
  public boolean requiresUpdateEveryTick() {return true;}
  public boolean isInterruptable(){return false;}

  public void tick() {
    int dummy = agent.tickCount - tickProgress;
    target = agent.getTarget();
    if (target == null || !target.isAlive()) {return;}
    agent.getLookControl().setLookAt(target);
    if (phase == 0){  // prep shield
      if (tickProgress == -1) {
        agent.setSpecialMove(0);
        tickProgress = agent.tickCount;
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
      } else if (dummy == 15) {
        agent.equipSpecial(true);
      } else if (dummy == 20) {
        agent.getSpecialSlot().getOrCreateTag().putLong("DeployTick", agent.level().getGameTime());
      } else if (dummy >= 80) {
        agent.level().playSound(null, agent, SoundEvents.VINDICATOR_CELEBRATE, SoundSource.BLOCKS, 1F, 1F);
        agent.setSpecialMove(1);
        tickProgress = agent.tickCount;
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
        phase++;
      }
    }
    else if (phase == 1){  // charge toward enemy
      if (dummy > MAX_CHASE_TIME) {
        phase = 7;
      } else if (agent.distanceToSqr(target) > 9) {
        agent.moveto(target, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue()*1.05);
      } else {
        agent.stopNav();
        agent.setSpecialMove(2);
        tickProgress = agent.tickCount;
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
        phase++;
      }
    }
    else if (phase == 2){  // taunt
      agent.level().playSound(null, agent, SoundEvents.EVOKER_CAST_SPELL, SoundSource.BLOCKS, 1F, 1F);
      AABB scanArea = agent.getBoundingBox().inflate(8); // 8-block radius
      List<Mob> enemies = agent.level().getEntitiesOfClass(Mob.class, scanArea, mob -> {
        return mob instanceof Monster && mob.isAlive();  // FIXME taunt other agent
      });
      for (Mob mob : enemies) {
        if (mob.getTarget() != null && mob.getTarget() != agent && mob.canAttack(agent)) {
          mob.setTarget(agent); // taunt
        }
      }
      // also make the target to be taunted
      if (target instanceof Mob mob) mob.setTarget(agent);
      phase++;
      tickProgress = agent.tickCount;
      agent.invincible = true;
    }
    else if (phase == 3){  // hold the line
      if (dummy >= 100){
        phase++;  // just skip for now
        tickProgress = agent.tickCount;
        agent.setSpecialMove(3);
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
      }
    }
    else if (phase == 4){  // shield bash
      if (dummy == 30){
        // enemy within 3 block within 180 degree ark get 4 dmg hurt + mc scaling damage + knockback 2
        Vec3 forward = agent.getLookAngle().normalize();
        AABB area = agent.getBoundingBox().inflate(3); // 3-block radius

        List<LivingEntity> targets = agent.level().getEntitiesOfClass(LivingEntity.class, area, e ->
            e != agent &&
                e.isAlive() &&
                !agent.sameTeam(e) &&
                e.distanceToSqr(agent) <= 9 && // 3 block radius squared
                isInFront(agent.position(), forward, e.position())
        );
        agent.level().playSound(null, agent, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1F, 1F);

        for (LivingEntity target : targets) {
          target.hurt(agent.damageSources().mobAttack(agent), (float) (4 + agent.getAttributeValue(Attributes.ATTACK_DAMAGE)));
          target.knockback(2F, agent.getX() - target.getX(), agent.getZ() - target.getZ()); // Knockback 2 ?
        }
      }
      else if (dummy >= 50) {
        agent.invincible = false;
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
        tickProgress = agent.tickCount;
        if (agent.inventory.gunExistWithAmmo()){
          agent.setSpecialMove(5);
          agent.equipGun();
          phase+=2;
        }
        else {
          agent.setSpecialMove(4);
          phase++;
        }
      }
    }
    else if (phase == 5){  // recover
      if (dummy >= 10){phase+=2;}
    }
    else if (phase == 6){  // gun burst
      if (dummy == 20 || dummy == 30 || dummy == 40) {
        agent.shootGun(false);
      } else if (dummy >= 60) {phase++;}
    }
  }
  protected boolean isInFront(Vec3 selfPos, Vec3 forward, Vec3 targetPos) {
    Vec3 toTarget = targetPos.subtract(selfPos).normalize();
    double dot = forward.dot(toTarget); // cos(theta)
    double angle = Math.acos(dot) * (180 / Math.PI);
    return angle <= (float) 180 / 2;
  }
}
