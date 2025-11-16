package com.github.ptran779.aegisops.brain.behavior;

import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

// make eating animation
public class EatFood extends AbsAction {
  AbstractAgentEntity agent;
  int food_val;
  EatFood(AbstractAgentEntity agent) {
    setFlags(EnumSet.of(Flag.USE));
    this.agent = agent;
  }

  public boolean keepUsing() {return agent.isUsingItem();}

  public void start() {
    ItemStack food = agent.inventory.getBestFood();
    food_val = food.getItem().getFoodProperties(food, agent).getNutrition();
    agent.setItemSlot(EquipmentSlot.OFFHAND, food);
    agent.startUsingItem(InteractionHand.OFF_HAND);
    agent.setKeepEating(true);
  }

  public void tick() {}

  public void end(){
    agent.setFood(Math.min(agent.getFood() + food_val, agent.maxfood));
    agent.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    agent.setKeepEating(agent.getFood() < agent.maxfood*0.9 && agent.inventory.checkFood());
  }
}
