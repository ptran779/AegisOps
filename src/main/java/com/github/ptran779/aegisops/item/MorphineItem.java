package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class MorphineItem extends Item implements IHealItem {
  public MorphineItem(Properties pProperties) {
    super(pProperties);
  }

  public boolean canHeal(LivingEntity entity) {return entity.getHealth() < entity.getMaxHealth() * 0.25;}
  public void heal(LivingEntity entity, ItemStack stack) {
    entity.heal(20); // set config
    stack.shrink(1);
    ((ServerLevel) entity.level()).sendParticles(ParticleTypes.HEART, entity.getX(), entity.getY() + 1.8, entity.getZ(), 5, 0, 1, 0, 0.02);
  }
  public int getAniMove() {return 1;}
  public boolean computeEffect(LivingEntity target, int tickcount, ItemStack stack) {
    if (tickcount == 15) {
      target.level().playSound(null, target, SoundEvents.HONEY_DRINK, SoundSource.BLOCKS, 1f, 1.0f);
      return false;
    } else if (tickcount >= 55) {
      heal(target, stack);
      return true;
    } else return false;
  }

  public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
    pTooltipComponents.add(Component.literal("Medic Special Item, use to heal nearby ally"));
    super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
  }
}

