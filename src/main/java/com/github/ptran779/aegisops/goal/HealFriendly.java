package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.github.ptran779.aegisops.item.HealItemI;
import com.github.ptran779.aegisops.network.AgentRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

public class HealFriendly extends AbstractThrottleGoal {
  AbstractAgentEntity agent;
  AbstractAgentEntity nearbyAgent;
  protected int tickProgress = -1;

  public HealFriendly(AbstractAgentEntity agent, int checkInterval) {
    super(agent, checkInterval);
    this.agent = agent;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.TARGET));
  }

  public boolean canUse() {
    if (!agent.getAllowSpecial() || !(nearbyAgent == null)) {return false;}
    if (!super.canUse()) return false;
    if (!(agent.getSpecialSlot().getItem() instanceof HealItemI healitem)){return false;}
    nearbyAgent = Utils.findNearestEntity(agent, AbstractAgentEntity.class, 16, entity ->
        entity != agent && agent.isFriendlyMod(entity) && (healitem.canHeal(entity)));
    return nearbyAgent != null && nearbyAgent.isAlive();
  }

  public boolean canContinueToUse(){
    return (nearbyAgent != null && nearbyAgent.isAlive() && (agent.hurtTime > 20 || agent.hurtTime == 0));
  }

  public void start() {
    tickProgress = -1;
  }
  public void stop() {
    nearbyAgent = null;
    agent.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    agent.setAniMove(Utils.AniMove.NORM);
  }
  public boolean requiresUpdateEveryTick() {return true;}
  public boolean isInterruptable(){return false;}

  public void tick() {
    if (nearbyAgent == null) {return;}
    int dummy = agent.tickCount - tickProgress;
    agent.getLookControl().setLookAt(nearbyAgent);

    if (agent.distanceToSqr(nearbyAgent) > 4) {
      if(!agent.moveto(nearbyAgent, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue())) nearbyAgent = null;
    } else if (agent.getSpecialSlot().getItem() instanceof HealItemI healItem){
      if (tickProgress == -1) {
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent), new AgentRenderPacket(agent.getId(), 1));
        agent.setAniMove(healItem.getAniMove());
        tickProgress = agent.tickCount;
        agent.equipSpecial();
      } else {
        if(healItem.computeEffect(nearbyAgent, dummy, agent.getSpecialSlot())) nearbyAgent = null;  // complete healing sequence
      }
    } else {nearbyAgent = null;}  // no cheating pls
  }
}
