package com.github.ptran779.aegisops.brain.agent;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.brain.api.Sensor;
import com.github.ptran779.aegisops.brain.api.ThrottleBehavior;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.network.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class SaluteBehavior extends ThrottleBehavior {
  protected int saluteTimer = 0;
  boolean flag = false;  // if boss already been seen within x radius
  Sensor<Player> bossS;
  Player boss;
  AbstractAgentEntity agent;

  public SaluteBehavior(AbstractAgentEntity agent, int cooldown, int varCooldown, Sensor<Player> bossS) {
    super(cooldown, varCooldown, agent);
    this.agent = agent;
    this.bossS = bossS;
  }

  @Override
  public boolean canUse() {
//    if (flag) return agent.tickCount - saluteTimer < 60;
    if (!super.canUse()) return false;
    if (agent.getBossUUID() == null) return false;

    boss = bossS.get();
    if (flag) {
      if (boss == null) {
        flag = false;
      }
      return false;
    }
    return boss != null;
  }
  @Override
  public boolean run() {
    if (boss == null) {return true;}
    agent.getLookControl().setLookAt(boss);
    return agent.tickCount - saluteTimer >= 60;
  }

  public void start(){
    flag = true;
    saluteTimer = agent.tickCount;
    agent.stopNav();
    agent.setAniMove(Utils.AniMove.SALUTE);
    PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent), new EntityRenderPacket(agent.getId(), 1));
  }

  public void stop() {
    super.stop();
    agent.setAniMove(Utils.AniMove.NORM);
  }
}
