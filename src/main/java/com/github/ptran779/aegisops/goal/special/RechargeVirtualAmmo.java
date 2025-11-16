package com.github.ptran779.aegisops.goal.special;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.structure.PortDisp;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.goal.AbstractThrottleGoal;
import com.github.ptran779.aegisops.network.render.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

import static com.github.ptran779.aegisops.config.ServerConfig.VIRT_AMMO_REFILL;

public class RechargeVirtualAmmo extends AbstractThrottleGoal {
  AbstractAgentEntity agent;
  PortDisp portDisp;
  protected int tickProgress = -1;

  public RechargeVirtualAmmo(AbstractAgentEntity agent, int checkInterval) {
    super(agent, checkInterval);
    this.agent = agent;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.TARGET));
  }

  @Override
  public boolean canUse() {
    if (!super.canUse()) return false;
    if (agent.getVirtualAmmo() > agent.getMaxVirtualAmmo()*0.8){return false;} // refill when less than 20%
    portDisp = Utils.findNearestEntity(agent, PortDisp.class, 16, entity ->
        entity.isFriendlyMod(agent, agent.level()) && entity.charge >= agent.getAmmoPerCharge());
    resetThrottle();
    return portDisp != null && portDisp.isAlive();
  }

  public boolean canContinueToUse(){return (portDisp != null && portDisp.isAlive());}
  public void start() {
    agent.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    tickProgress = -1;
  }
  public void stop() {
    portDisp = null;  // ensure clean
    agent.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    agent.setAniMove(Utils.AniMove.NORM);
  }
  public boolean requiresUpdateEveryTick() {return true;}

  public void tick() {
    // god know why
    if (portDisp == null) {return;}
    int dummy = agent.tickCount - tickProgress;

    agent.getLookControl().setLookAt(portDisp);
    if (agent.distanceToSqr(portDisp) > 3) {
      if(!(agent.moveto(portDisp, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue()))) {portDisp = null;};
    } else if (tickProgress == -1) {
      PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
      agent.setAniMove(Utils.AniMove.DISP_RELOAD);
      tickProgress = agent.tickCount;
      portDisp.setOpen(true);
    } else if (dummy == 20 || dummy == 30 || dummy == 40 || dummy == 50) {
      agent.level().playSound(null, agent, SoundEvents.DRIPSTONE_BLOCK_BREAK, SoundSource.BLOCKS, 1.2f, 0.5f);
    } else if (dummy > 60){
      ((ServerLevel) agent.level()).sendParticles(ParticleTypes.END_ROD, agent.getX(), agent.getY()+1, agent.getZ(), 10, 0, 1, 0, 0.02);
      int reloadAmount = Math.min(
          Math.min(portDisp.charge / agent.getAmmoPerCharge(), agent.getMaxVirtualAmmo() - agent.getVirtualAmmo()),
          VIRT_AMMO_REFILL.get());
      agent.setVirtualAmmo(agent.getVirtualAmmo() + reloadAmount);
      portDisp.charge -= reloadAmount * agent.getAmmoPerCharge();
      portDisp = null;
    }
  }
}
