package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.Utils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IHealItem {
  boolean canHeal(LivingEntity target);
  void heal(LivingEntity target, ItemStack stack);
  int getAniMove();
  boolean computeEffect(LivingEntity target, int tickcount, ItemStack stack);
}
