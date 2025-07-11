package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class Morphine extends Item implements HealItemI{
  public Morphine(Properties pProperties) {
    super(pProperties);
  }

  public boolean canHeal(LivingEntity entity) {return entity.getHealth() < entity.getMaxHealth() * 0.25;}
  public void heal(LivingEntity entity, ItemStack stack) {
    entity.heal(20); // set config
    stack.shrink(1);
    ((ServerLevel) entity.level()).sendParticles(ParticleTypes.HEART, entity.getX(), entity.getY() + 1.8, entity.getZ(), 5, 0, 1, 0, 0.02);
  }
  public Utils.AniMove getAniMove() {return Utils.AniMove.SPECIAL1;}
  public boolean computeEffect(LivingEntity target, int tickcount, ItemStack stack) {
    if (tickcount == 15) {
      target.level().playSound(null, target, SoundEvents.HONEY_DRINK, SoundSource.BLOCKS, 1f, 1.0f);
      return false;
    } else if (tickcount >= 55) {
      heal(target, stack);
      return true;
    } else return false;
  }
}

