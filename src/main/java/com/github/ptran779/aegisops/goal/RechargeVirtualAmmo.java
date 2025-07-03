package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.PortDisp;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.github.ptran779.aegisops.network.AgentRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

import static com.github.ptran779.aegisops.Config.ServerConfig.VIRT_AMMO_REFILL;

public class RechargeVirtualAmmo extends Goal {
  AbstractAgentEntity agent;
  PortDisp portDisp;
  protected int checkInterval;
  private int checkTime=0;
  protected int tickProgress = -1;

  public RechargeVirtualAmmo(AbstractAgentEntity agent, int checkInterval) {
    this.agent = agent;
    this.checkInterval = checkInterval;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.TARGET));
  }

  @Override
  public boolean canUse() {
    if (agent.tickCount - checkTime < checkInterval) {return false;}
    checkTime = agent.tickCount;  // reset counter
    if (!(agent.getGunSlot().getItem() instanceof ModernKineticGunItem)){return false;}
    if (agent.getVirtualAmmo() + agent.getAmmoPerCharge() > agent.getMaxVirtualAmmo()){return false;}
    portDisp = Utils.findNearestEntity(agent, PortDisp.class, 16, entity ->
        entity.isFriendlyMod(agent) && entity.charge >= agent.getAmmoPerCharge());
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
      agent.moveto(portDisp, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
    } else if (tickProgress == -1) {
      PacketHandler.CHANNELS.send(
          PacketDistributor.TRACKING_ENTITY.with(() -> agent),
          new AgentRenderPacket(agent.getId(), 1)
      );
      agent.setAniMove(Utils.AniMove.DISP_RELOAD);
      tickProgress = agent.tickCount;
      portDisp.setOpen(true);
//      PacketHandler.CHANNELS.send(
//          PacketDistributor.TRACKING_ENTITY.with(() -> portDisp),
//          new StructureRenderPacket(portDisp.getId(), 0.0f)
//      );
    } else if (dummy > 60){   ///  FIXME animation and effect and sound
      int reloadAmount = Math.min(
          Math.min(portDisp.charge / agent.getAmmoPerCharge(), agent.getMaxVirtualAmmo() - agent.getVirtualAmmo()),
          VIRT_AMMO_REFILL.get());
      agent.setVirtualAmmo(agent.getVirtualAmmo() + reloadAmount);
      portDisp.charge -= reloadAmount * agent.getAmmoPerCharge();
      portDisp = null;
    }
  }
}
