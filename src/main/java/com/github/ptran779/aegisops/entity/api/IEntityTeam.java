package com.github.ptran779.aegisops.entity.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

// for agent and turret
public interface IEntityTeam {
  default boolean isBoss(Player player) {
    if (getBossUUID() == null) {return false;}
    return (getBossUUID().equals(player.getUUID()));
  }

  default boolean isFriendlyPlayer(Player player, Level level) {
    // 1. No Boss? Not friendly.
    if (getBossUUID() == null) return false;
    // 2. Is the player the Boss? Friendly.
    if (getBossUUID().equals(player.getUUID())) return true;
    // 3. Does the TARGET player have a team? If no, cannot be teammates.
    if (player.getTeam() == null) return false;
    // 4. Is the BOSS online? If not, we can't check their team.
    Player boss = level.getPlayerByUUID(getBossUUID());
    if (boss == null) return false;
    // 5. Does the BOSS have a team?
    if (boss.getTeam() == null) return false;
    // 6. Compare teams
    return player.getTeam().getName().equals(boss.getTeam().getName());
  }

  default boolean isFriendlyMod(IEntityTeam teamer, Level level) {
    // 1. Missing UUIDs?
    if (getBossUUID() == null || teamer.getBossUUID() == null) return false;
    // 2. Same Boss? Friendly.
    if (getBossUUID().equals(teamer.getBossUUID())) return true;
    // 3. Get both Boss entities
    Player myBoss = level.getPlayerByUUID(getBossUUID());
    Player otherBoss = level.getPlayerByUUID(teamer.getBossUUID());

    // 4. Are both bosses online?
    if (myBoss == null || otherBoss == null) return false;
    // 5. Do both bosses have teams?
    if (myBoss.getTeam() == null || otherBoss.getTeam() == null) return false;
    // 6. Compare teams
    return myBoss.getTeam().getName().equals(otherBoss.getTeam().getName());
  }
  UUID getBossUUID();
}
