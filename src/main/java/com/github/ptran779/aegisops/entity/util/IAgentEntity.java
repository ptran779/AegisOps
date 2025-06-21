package com.github.ptran779.aegisops.entity.util;

import net.minecraft.world.item.ItemStack;

// support interface that require implementation at the final class level, but not abstract
public interface IAgentEntity {
  public boolean isEquipableGun(ItemStack item);
}
