package com.github.ptran779.aegisops.entity.util;

import net.minecraft.world.item.ItemStack;

// support interface that require implementation at the final class level, but not abstract
public interface IAgentEntity {
  boolean isEquipableGun(ItemStack item);

  boolean isEquipableMelee(ItemStack item);
}
