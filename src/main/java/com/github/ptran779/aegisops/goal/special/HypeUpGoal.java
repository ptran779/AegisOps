package com.github.ptran779.aegisops.goal.special;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.goal.AbstractThrottleGoal;
import com.github.ptran779.aegisops.network.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.github.ptran779.aegisops.server.EffectInit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

public class HypeUpGoal extends AbstractThrottleGoal {
  private final AbstractAgentEntity agent;
  private int tickAction = -1;
  private boolean done = false;
  public HypeUpGoal(AbstractAgentEntity agent, int checkInterval) {
    super(agent, checkInterval);
    this.agent = agent;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
  }

  public boolean canUse() {
    if (!agent.getAllowSpecial()) {return false;}
    return (agent.getTarget() != null && super.canUse() && agent.haveWeapon());
  }

  public boolean canContinueToUse() {return !done;}
  public boolean isInterruptable(){return false;}
  public void start() {
    done = false;
    this.resetThrottle();
    tickAction = agent.tickCount;
    PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent),new EntityRenderPacket(agent.getId(), 1));
  }
  public void stop() {agent.setAniMove(Utils.AniMove.NORM);}

  public void tick() {
    if (agent.tickCount - tickAction > 25) {
      agent.addEffect(new MobEffectInstance(EffectInit.BATTLE_FLOW.get(),200,1));
      done = true;
    }
  }
}
