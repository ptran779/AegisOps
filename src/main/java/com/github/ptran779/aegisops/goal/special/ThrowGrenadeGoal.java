package com.github.ptran779.aegisops.goal.special;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.entity.extra.Grenade;
import com.github.ptran779.aegisops.goal.AbstractThrottleGoal;
import com.github.ptran779.aegisops.item.GrenadeItem;
import com.github.ptran779.aegisops.network.render.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.github.ptran779.aegisops.server.EntityInit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

import static com.github.ptran779.aegisops.config.ServerConfig.GRENADE_CLUSTER;

public class ThrowGrenadeGoal extends AbstractThrottleGoal {
  AbstractAgentEntity agent;
  protected int lastCooldown;
  protected final int COOLDOWN;
  protected final int CLUSTER_RADIUS = 3;
  protected boolean done;
  protected LivingEntity target;
  // candidate launch angles
  public final double[] THROW_ANGLES = {15, 30, 45, 60, 75};
  public final double RAY_ANGLE_OFFSET = 5;
  public final double SCAN_DISTANCE = 6;

//  protected int tickAction
public ThrowGrenadeGoal(AbstractAgentEntity agent, int checkInterval, int cooldown) {
    super(agent, checkInterval);
    this.agent = agent;
    this.COOLDOWN = cooldown;
    this.lastCooldown = agent.tickCount;
  this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
  }

  public boolean canUse() {
    // basic check
    if ((!agent.getAllowSpecial() ||
        agent.tickCount - lastCooldown < COOLDOWN) ||
        !(agent.getSpecialSlot().getItem() instanceof GrenadeItem) ||
        !super.canUse())
    {return false;}
    resetThrottle();
    // check for enemy target
    target = agent.getTarget();
    if (target == null || !target.isAlive()) return false;

    int clusterCount = agent.level().getEntitiesOfClass(LivingEntity.class,
            target.getBoundingBox().inflate(CLUSTER_RADIUS),
            (e) -> (agent.shouldTargetEntity(agent, e)))
        .size();
    return clusterCount >= GRENADE_CLUSTER.get();
  }

  public boolean requiresUpdateEveryTick() {return true;}
  public boolean isInterruptable(){return false;}
  public void start() {
    agent.setSpecialMove(0);
    PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
    lastCooldown = agent.tickCount;
    done = false;
    target = agent.getTarget();
  }

  public boolean canContinueToUse() {
    return !done && agent.getSpecialSlot().getItem() instanceof GrenadeItem;
  }

  public void stop() {
    agent.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    agent.setAniMove(Utils.AniMove.NORM);
    lastCooldown = agent.tickCount;
  }

  public void tick() {
    if (target == null || !target.isAlive()) {
      done = true;
      return;
    }
    int dummy = agent.tickCount - lastCooldown;
    agent.getLookControl().setLookAt(target);
    if (dummy == 3) {
      agent.equipSpecial(false);
    } else if (dummy == 25) {
      agent.getSpecialSlot().getOrCreateTag().putLong("DeployTick", agent.level().getGameTime());
    } else if (dummy == 60) {
      // compute velocity depend on target length away
      Vec3 from = agent.getEyePosition();
      Vec3 delta = target.position().add(0,1,0).subtract(from);
      double dxz = Math.hypot(delta.x, delta.z);
      if (dxz < 1e-6) return;  // too close

      Vec3 flat = new Vec3(delta.x/dxz, 0, delta.z/dxz);
      double speedNeeded = -1, angleNeeded = 0;

      for (double angle : THROW_ANGLES) {
        // 1) ray at (angle − offset)
        double testRad = Math.toRadians(angle - RAY_ANGLE_OFFSET);
        double cosT = Math.cos(testRad), sinT = Math.sin(testRad);
        Vec3 testDir = new Vec3(flat.x * cosT, sinT, flat.z * cosT).normalize();
        Vec3 rayEnd = from.add(testDir.scale(SCAN_DISTANCE));
        if (agent.level().clip(new ClipContext(from, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getType() != HitResult.Type.MISS) continue;

        // 2) solve for speed at actual angle
        double pitch = Math.toRadians(angle);
        double cosP = Math.cos(pitch);
        double tanP = Math.tan(pitch);
        double denom = 2 * (dxz * tanP - delta.y) * cosP * cosP;
        if (denom <= 0) continue;
        double v2 = - Grenade.GRAVITY * dxz*dxz / denom;
        if (v2 <= 0) continue;

        double speed = Math.sqrt(v2);
        if (speed > GrenadeItem.MaxThrowSpeed) {continue;}

        speedNeeded = speed;
        angleNeeded = angle;
        break;
      }

      if (speedNeeded > 0) {
        Vec3 throwDir = new Vec3(
            flat.x * Math.cos(Math.toRadians(angleNeeded)),
            Math.sin(Math.toRadians(angleNeeded)),
            flat.z * Math.cos(Math.toRadians(angleNeeded))
        ).normalize();
        Grenade g = new Grenade(EntityInit.GRENADE.get(), agent.level());
        g.setPos(agent.getX(), agent.getEyeY()-0.1, agent.getZ());
        g.setFuseTick(100);
        g.setDeltaMovement(throwDir.scale(speedNeeded));
        agent.level().addFreshEntity(g);
        agent.getSpecialSlot().shrink(1);
        agent.getSpecialSlot().getOrCreateTag().remove("DeployTick");
      }

      agent.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    } else if (dummy >= 90) {
      done = true;
    }
  }
}
