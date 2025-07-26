package com.github.ptran779.aegisops.entity.api;

import com.github.ptran779.aegisops.Utils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

// for agent and turret
public interface IEntityTarget {
  boolean haveWeapon();
  LivingEntity getTarget();
  Utils.TargetMode getTargetMode();
  void setTargetMode(Utils.TargetMode mode);
  Utils.TargetMode nextTargetMode();
  default boolean shouldTargetEntity(IEntityTeam user, LivingEntity entity) {
    boolean out = switch (getTargetMode()) {
      case OFF -> false;
      case HOSTILE_ONLY -> entity instanceof Enemy;
      case ENEMY_AGENTS -> {
        if (entity instanceof Player player) yield !user.isFriendlyPlayer(player, entity.level());
        if (entity instanceof IEntityTeam teamer) yield !user.isFriendlyMod(teamer, entity.level());
        yield false;
      }
      case ALL -> {
        if (entity instanceof Enemy) yield true;
        if (entity instanceof Player player) yield !user.isFriendlyPlayer(player, entity.level());
        if (entity instanceof IEntityTeam teamer) yield !user.isFriendlyMod(teamer, entity.level());
        yield false;
      }
    };
    return out;
  }
}
