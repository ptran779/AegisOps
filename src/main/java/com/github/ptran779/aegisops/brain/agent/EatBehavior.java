package com.github.ptran779.aegisops.brain.agent;

import com.github.ptran779.aegisops.brain.api.Behavior;
import com.github.ptran779.aegisops.brain.api.Sensor;
import com.github.ptran779.aegisops.client.animation.AnimationLibrary;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.network.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

public class EatBehavior extends Behavior {
  AbstractAgentEntity agent;
  int food_val=0;
  Sensor<ItemStack> itemStackSensor;
  ItemStack food = null;

  private int dummy;

  public EatBehavior(AbstractAgentEntity agent, Sensor<ItemStack> itemStackSensor) {
    this.agent = agent;
    this.itemStackSensor = itemStackSensor;
  }

  public void start() {
    dummy = 0;
    agent.setAniMoveStatic(AnimationLibrary.A_EATING);
    PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> agent), new EntityRenderPacket(agent.getId(), 1));
  }

  @Override
  public boolean canUse() {
    food = itemStackSensor.get();
    if (food.isEmpty()) return false;
    food_val = food.getItem().getFoodProperties(food, agent).getNutrition();
    return (agent.getFood() + food_val <= agent.maxfood);
  }

  @Override
  public boolean run() {
//    if (agent.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {return true;}
    dummy++;
    if (dummy == 10) {agent.setItemSlot(EquipmentSlot.OFFHAND, food);}
    else if (dummy == 30 || dummy == 40 || dummy == 50) {
      agent.level().playSound(null, agent, SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0f, 1.0f);
    } else if (dummy == 60) {
      // check to see if my food still there
      if (agent.getItemInHand(InteractionHand.OFF_HAND).isEmpty()){return true;}
      food.shrink(1);
      agent.setFood(Math.min(agent.getFood() + food_val, agent.maxfood));
      agent.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
      ((ServerLevel) agent.level()).sendParticles(ParticleTypes.COMPOSTER,
          agent.getX() + 0.5,
          agent.getY() + 1.8,
          agent.getZ() + 0.5,
          12, 0.25, 0.25, 0.25, 0.02);
    } else return dummy > 90;
    return false;
  }

  public void stop(){
    agent.setAniMoveStatic(AnimationLibrary.A_LIVING);
  }

  public void interrupt(){
    agent.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
  }
}
