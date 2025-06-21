package com.github.ptran779.aegisops.goal;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

public class EatFoodGoal extends Goal {
  AbstractAgentEntity agent;
  int food_val=0;

  public EatFoodGoal(AbstractAgentEntity agent) {
    this.agent = agent;
  }

  @Override
  public boolean canUse() {
    return (agent.getFood() < agent.maxfood*0.5f || agent.getKeepEating()) && agent.inventory.checkFood();
  }

  @Override
  public void start() {
    ItemStack food = agent.inventory.getBestFood();
    food_val = food.getItem().getFoodProperties(food, agent).getNutrition();
    agent.setItemSlot(EquipmentSlot.OFFHAND, food);
    agent.startUsingItem(InteractionHand.OFF_HAND);
    agent.setKeepEating(true);
  }

  public boolean canContinueToUse() {
    return agent.isUsingItem();
  }

  public void stop(){
    agent.setFood(Math.min(agent.getFood() + food_val, agent.maxfood));
    agent.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    agent.setKeepEating(agent.getFood() < agent.maxfood*0.9 && agent.inventory.checkFood());
  }
}
