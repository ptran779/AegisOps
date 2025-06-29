package com.github.ptran779.aegisops.entity.util;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MeleeSlot extends Slot {
  private final AbstractAgentEntity agent;
  public MeleeSlot(AgentInventory agentInventory, int pSlot, int pX, int pY) {
    super(agentInventory, pSlot, pX, pY);
    this.agent = agentInventory.getAgent();
  }

  @Override
  public boolean mayPlace(ItemStack pStack) {return agent.isEquipableMelee(pStack);}
}
