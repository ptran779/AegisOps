package com.github.ptran779.aegisops.goal.special;

import com.github.ptran779.aegisops.config.ServerConfig;
import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.structure.AbstractAgentStruct;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.goal.AbstractThrottleGoal;
import com.github.ptran779.aegisops.item.EngiHammerItem;
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

public class WorkOnStructureGoal extends AbstractThrottleGoal {
  AbstractAgentEntity agent;
  AbstractAgentStruct aStruct;
  protected int tickProgress = -1;

  public WorkOnStructureGoal(AbstractAgentEntity agent, int checkInterval) {
    super(agent, checkInterval);
    this.agent = agent;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.TARGET));
  }

  public boolean canUse() {
    if (!agent.getAllowSpecial() || aStruct != null) {return false;}
    if (!super.canUse()) return false;
    if (!(agent.getSpecialSlot().getItem() instanceof EngiHammerItem)){return false;}
    aStruct = Utils.findNearestEntity(agent, AbstractAgentStruct.class, 16, entity ->
        entity.isFriendlyMod(agent, agent.level()) && (entity.charge + ServerConfig.ENGI_WORK_RECHARGE.get() <= entity.getMaxCharge()));
    resetThrottle();
    return aStruct != null && aStruct.isAlive();
  }

  public boolean canContinueToUse(){
    return (aStruct != null && aStruct.isAlive() && (agent.hurtTime > 20 || agent.hurtTime == 0));
  }

  public void start() {
    agent.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    tickProgress = -1;
  }
  public void stop() {
    aStruct = null;  // ensure clean
    agent.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    agent.setAniMove(Utils.AniMove.NORM);
  }
  public boolean requiresUpdateEveryTick() {return true;}
  public boolean isInterruptable(){return false;}

  public void tick(){
    // god know why
    if (aStruct == null) {return;}
    int dummy = agent.tickCount - tickProgress;

    agent.getLookControl().setLookAt(aStruct);
    if (agent.distanceToSqr(aStruct) > 4) {
      if(!agent.moveto(aStruct, agent.getAttribute(Attributes.MOVEMENT_SPEED).getValue())) aStruct=null;
    } else if (tickProgress == -1) {
      PacketHandler.CHANNELS.send(
          PacketDistributor.TRACKING_ENTITY.with(() -> agent),
          new EntityRenderPacket(agent.getId(), 1)
      );
      agent.setSpecialMove(0);
      tickProgress = agent.tickCount;
    } else if (dummy == 10 || dummy == 20 || dummy == 30 || dummy == 40) {
      agent.level().playSound(null, aStruct, SoundEvents.DRIPSTONE_BLOCK_BREAK, SoundSource.BLOCKS, 1f, 1.0f);
    } else if (dummy == 60) {
      agent.equipSpecial(false);
    } else if (dummy == 85 || dummy == 100 || dummy == 115) {
      agent.level().playSound(null, aStruct, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.8f, 1.0f);
      ((ServerLevel) agent.level()).sendParticles(ParticleTypes.SCRAPE, aStruct.getX(), aStruct.getY()+1.8, aStruct.getZ(), 10, 0, 1, 0, 0.02);
    } else if (dummy > 120) {
      aStruct.charge = Math.min(aStruct.charge +20, aStruct.getMaxCharge());
      if (aStruct.getHealth() < aStruct.getMaxHealth()) {aStruct.heal(2);}
      aStruct = null;
    }
  }
}
