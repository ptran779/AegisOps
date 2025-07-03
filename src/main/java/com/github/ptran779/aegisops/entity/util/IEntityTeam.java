package com.github.ptran779.aegisops.entity.util;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

// for agent and turret
public interface IEntityTeam {
  boolean isFriendlyPlayer(Player player);
  boolean isFriendlyMod(IEntityTeam teamer);
  UUID getBossUUID();
}
