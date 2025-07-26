package com.github.ptran779.aegisops.entity.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

// for agent and turret
public interface IEntityTeam {
  default boolean isFriendlyPlayer(Player player, Level level) {
    // Boss
    if (getBossUUID() == null) {return false;}
    if (getBossUUID().equals(player.getUUID())) return true;
    // Team
    return player.getTeam().getName().equals(level.getPlayerByUUID(getBossUUID()).getTeam().getName());  // same team
  };
  default boolean isFriendlyMod(IEntityTeam teamer, Level level){
    // boss
    if (getBossUUID() == null || teamer.getBossUUID() == null) {return false;}
    if (getBossUUID().equals(teamer.getBossUUID())) {return true;}
    //team with boss
    return level.getPlayerByUUID(teamer.getBossUUID()).getTeam().getName().equals(level.getPlayerByUUID(getBossUUID()).getTeam().getName());
  };
  UUID getBossUUID();
}
