package com.github.ptran779.aegisops.entity.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArmorSlot extends Slot {
  private final EquipmentSlot slotType;
  private final AbstractAgentEntity agent;
  public ArmorSlot(AgentInventory agentInventory, int pSlot, int pX, int pY, EquipmentSlot pSlotType) {
    super(agentInventory, pSlot, pX, pY);
    this.slotType = pSlotType;
    this.agent = agentInventory.getAgent();
  }

  @Override
  public boolean mayPlace(ItemStack pStack) {return pStack.canEquip(slotType, agent);}
}
