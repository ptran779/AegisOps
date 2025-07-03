package com.github.ptran779.aegisops.entity.util;

import com.github.ptran779.aegisops.Utils;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

// for agent and turret
public interface IEntityTarget {
  boolean haveWeapon();
  LivingEntity getTarget();
  Utils.TargetMode getTargetMode();
  void setTargetMode(Utils.TargetMode mode);
  Utils.TargetMode nextTargetMode();
  boolean shouldTargetEntity(LivingEntity entity);
  void clearTarget();
}
