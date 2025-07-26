package com.github.ptran779.aegisops.goal.special;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.entity.extra.VectorPursuer;
import com.github.ptran779.aegisops.goal.AbstractThrottleGoal;
import com.github.ptran779.aegisops.item.VPTerminalItem;
import com.github.ptran779.aegisops.network.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.github.ptran779.aegisops.server.EntityInit;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

import static com.github.ptran779.aegisops.Config.ServerConfig.VP_MIN_TARGET_HEALTH;

public class DeployVPGoal extends AbstractThrottleGoal {
  AbstractAgentEntity agent;
  protected int lastCooldown;
  protected final int COOLDOWN;
  public static final double SCAN_DISTANCE = 40;
  protected boolean done = false;
  LivingEntity target;

//  protected int tickAction
  public DeployVPGoal(AbstractAgentEntity agent, int checkInterval, int cooldown) {
    super(agent, checkInterval);
    this.agent = agent;
    this.COOLDOWN = cooldown;
    this.lastCooldown = agent.tickCount;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
  }

  public boolean canUse() {
    // basic check
    if ((!agent.getAllowSpecial() ||
        agent.getTargetMode() == Utils.TargetMode.OFF ||
        agent.tickCount - lastCooldown < COOLDOWN) ||
        !(agent.getSpecialSlot().getItem() instanceof VPTerminalItem) ||
        !super.canUse())
    {return false;}
    resetThrottle();
    // check for enemy target
    target = Utils.findNearestEntity(agent, LivingEntity.class, SCAN_DISTANCE, entity -> entity.getHealth() >= VP_MIN_TARGET_HEALTH.get() && agent.shouldTargetEntity(agent, entity));
    return target != null && target.isAlive();
  }
  public boolean requiresUpdateEveryTick() {return true;}
  public boolean isInterruptable(){return false;}
  public void start() {
    agent.setSpecialMove(1);
    PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
    lastCooldown = agent.tickCount;
    done = false;
  }
  public boolean canContinueToUse() {
    return !done && agent.getSpecialSlot().getItem() instanceof VPTerminalItem;
  }
  public void stop() {
    agent.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
    agent.setAniMove(Utils.AniMove.NORM);
    lastCooldown = agent.tickCount;
  }

  public void tick() {
    int dummy = agent.tickCount - lastCooldown;
    if (target == null || !target.isAlive()){
      done = true;
      return;
    }
    agent.getLookControl().setLookAt(target);
    if (dummy == 10) {
      agent.equipSpecial(true);
      agent.level().playSound(null, agent.getX(), agent.getY(), agent.getZ(), SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS, 1.0F, 1.25F);
    } else if (dummy == 30) {
      agent.level().playSound(null, agent.getX(), agent.getY(), agent.getZ(),SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS, 1.0F, 1.25F);
    } else if (dummy == 20 || dummy == 40) {
      agent.level().playSound(null, agent.getX(), agent.getY(), agent.getZ(),SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS, 1.0F, 0.75F);
    } else if (dummy == 50) {
      VectorPursuer drone = new VectorPursuer(EntityInit.VECTOR_PURSUER.get(), agent.level());
      agent.level().playSound(null, agent.getX(), agent.getY(), agent.getZ(),SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS, 1.0F, 1.25F);

      drone.setPos(agent.getX(), agent.getY()+1, agent.getZ());
      drone.deployerUUID = agent.getUUID();
      drone.setTarget(target);  // clear me
      agent.level().addFreshEntity(drone);
      agent.getSpecialSlot().shrink(1);
    } else if (dummy == 60) {
      done = true;
    }
  }
}
