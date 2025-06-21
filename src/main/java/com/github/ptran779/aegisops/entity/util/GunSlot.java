package com.github.ptran779.aegisops.entity.util;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GunSlot extends Slot {
  private final AbstractAgentEntity agent;
  public GunSlot(AgentInventory agentInventory, int pSlot, int pX, int pY) {
    super(agentInventory, pSlot, pX, pY);
    this.agent = agentInventory.getAgent();
  }

  @Override
  public boolean mayPlace(ItemStack pStack) {return agent.isEquipableGun(pStack);}
}
