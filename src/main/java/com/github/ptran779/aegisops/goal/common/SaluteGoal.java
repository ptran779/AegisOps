package com.github.ptran779.aegisops.goal.common;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.goal.AbstractThrottleGoal;
import com.github.ptran779.aegisops.network.render.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;

public class SaluteGoal extends AbstractThrottleGoal {
  boolean toggle = false;
  public static final int SALUTE_COOLDOWN = 1200;
  protected int saluteTimer = -SALUTE_COOLDOWN;
  Player boss = null;

  AbstractAgentEntity agent;
  public SaluteGoal(AbstractAgentEntity agent, int checkInterval) {
    super(agent, checkInterval);
    this.agent = agent;
    this.checkInterval = checkInterval;
    this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.TARGET));
  }

  public boolean canUse() {
    if (agent.getBossUUID() == null) return false;
    if (!super.canUse()) return false;

    boss = Utils.findNearestEntity(agent, Player.class, 8, entity -> agent.getBossUUID().equals(entity.getUUID()));
    resetThrottle();

    if (boss == null) {
      toggle = false;
      return false;
    }
    if (toggle || agent.tickCount - saluteTimer < SALUTE_COOLDOWN) return false;
    // Trigger
    toggle = true;
    saluteTimer = agent.tickCount;
    return true;
  }

  @Override
  public boolean canContinueToUse() {return agent.tickCount - saluteTimer < 60;}

  public void start() {
    agent.stopNav();
    PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent), new EntityRenderPacket(agent.getId(), 1));
    agent.setAniMove(Utils.AniMove.SALUTE);
  }
  public void stop() {agent.setAniMove(Utils.AniMove.NORM);}
  public void tick() {
    agent.getLookControl().setLookAt(boss);
  }
}
