package com.github.ptran779.aegisops.entity.util;

import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.ModernKineticGunItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class AgentInventory extends SimpleContainer {
  private final AbstractAgentEntity agent;
  private int lastAmmoSlot = -1;  // use in quick lookup for reload logic when needed
  public AgentInventory(int size, AbstractAgentEntity owner) {
    super(size);
    this.agent = owner;
  }

  /// ARMOR STUFF
  //Find best armor can buy
  protected void equipBestArmor(){
    // get current load
    //FIXME this thing not sync, it pull from entity instead of inventory. Careful if it overwrite and cause item lost
    //Actually, it maybe fine, Im not responsible for someone else bad design, but FYI
    ItemStack helmet = agent.getItemBySlot(EquipmentSlot.HEAD);
    ItemStack chest = agent.getItemBySlot(EquipmentSlot.CHEST);
    ItemStack legs = agent.getItemBySlot(EquipmentSlot.LEGS);
    ItemStack boots = agent.getItemBySlot(EquipmentSlot.FEET);
    // find best gear
    for (int i=0; i<getContainerSize(); i++){
      ItemStack stack = getItem(i);
      if (stack.getItem() instanceof ArmorItem armor){
        switch (armor.getEquipmentSlot()){
          case HEAD:
            if (helmet.isEmpty() || armor.getDefense() > ((ArmorItem)helmet.getItem()).getDefense()){
              helmet = stack;
              swapItem(agent.gearSlots[0], i);
              agent.setItemSlot(armor.getEquipmentSlot(), helmet);
            }
            break;
          case CHEST:
            if (chest.isEmpty() || armor.getDefense() > ((ArmorItem)chest.getItem()).getDefense()){
              chest = stack;
              swapItem(agent.gearSlots[1], i);
              agent.setItemSlot(armor.getEquipmentSlot(), chest);
            }
            break;
          case LEGS:
            if (legs.isEmpty() || armor.getDefense() > ((ArmorItem)legs.getItem()).getDefense()){
              legs = stack;
              swapItem(agent.gearSlots[2], i);
              agent.setItemSlot(armor.getEquipmentSlot(), legs);
            }
            break;
          case FEET:
            if (boots.isEmpty() || armor.getDefense() > ((ArmorItem)boots.getItem()).getDefense()){
              boots = stack;
              swapItem(agent.gearSlots[3], i);
              agent.setItemSlot(armor.getEquipmentSlot(), boots);
            }
            break;
        }
      }
    }
  }

  // put on the armor, depending on if using auto equip or just whatever in the slot.
  public void loadArmor(){
//    if (agent.getAllowSpecial()) {equipBestArmor();}
//    else {
    for (int i : agent.gearSlots) {
      ItemStack piece = getItem(i);
      if (piece.getItem() instanceof ArmorItem armor) {
        agent.setItemSlot(armor.getEquipmentSlot(), piece);}
//      }
    }
  }

  // external modification may equip living entity with armor. ex. dispenser. This call the itemstack to sync to inventory armor slot
  // empty slot will not overwrite
  public void syncArmor(){
    ItemStack [] fullLoad = {agent.getItemBySlot(EquipmentSlot.HEAD), agent.getItemBySlot(EquipmentSlot.CHEST), agent.getItemBySlot(EquipmentSlot.LEGS), agent.getItemBySlot(EquipmentSlot.FEET)};
    for (int i=0; i<4; i++) {
      if (fullLoad[i].getItem() instanceof ArmorItem) {setItem(agent.gearSlots[i], fullLoad[i]);}
    }
  }

  /// FOOD STUFF
  // any food here?
  public boolean checkFood(){
    for (int i=0; i<getContainerSize(); i++){if (getItem(i).isEdible()) {return true;}}
    return false;
  }
  //inv prety small, just find me the best one
  public ItemStack getBestFood() {
    ItemStack out = ItemStack.EMPTY;
    for (int i=0; i<getContainerSize(); i++) {
      ItemStack stack = getItem(i);
      if (stack.isEdible() && (!out.isEdible() || (stack.getItem().getFoodProperties(stack, agent).getNutrition() > out.getItem().getFoodProperties(stack, agent).getNutrition()))) {
        out = stack;
      }
    }
    return out;
  }

  /// MELEE STUFF
  //assume no autoequip for now
  public boolean meleeExist(){
    return !getItem(agent.meleeSlot).isEmpty();
  }

  /// Fire ARM :) // soft check gun type only. Strict check on menu.
  public boolean gunExistWithAmmo(){
    ItemStack stack = getItem(agent.gunSlot);
    if (stack.getItem() instanceof ModernKineticGunItem gunItem) {
      return checkGunAmmo(stack, gunItem) > 0 || agent.getVirtualAmmo() > 0 || findGunAmmo(stack) != -1;
    }
    return false;
  }

  public int checkGunAmmo(ItemStack gunStack, AbstractGunItem gunItem){return gunItem.getCurrentAmmoCount(gunStack);}

  private int isAmmoGunSlot(int slotId, ItemStack gunStack){
    ItemStack checkAmmoStack = getItem(slotId);
    if (checkAmmoStack.getItem() instanceof IAmmo iAmmo) {
      if (iAmmo.isAmmoOfGun(gunStack, checkAmmoStack)) {
        lastAmmoSlot = slotId;
        return slotId;
      }
    } else if (checkAmmoStack.getItem() instanceof IAmmoBox iAmmoBox) {
      if (iAmmoBox.isAmmoBoxOfGun(gunStack, checkAmmoStack)){
        lastAmmoSlot = slotId;
        return slotId;
      }
    }
    return -1;
  }

  public int findGunAmmo(ItemStack gunStack){
    int out;
    // check cache
    if (this.lastAmmoSlot != -1){
      out = isAmmoGunSlot(this.lastAmmoSlot, gunStack);
      if (out != -1){return out;}
    }
    //scan all inv
    for (int i=0; i<getContainerSize(); i++){
      out = isAmmoGunSlot(i, gunStack);
      if (out != -1){return out;}
    }
    return -1;
  };

  // quick check to see if there's weapon in our slot
  public boolean haveWeapon(){return gunExistWithAmmo() || meleeExist();}

  /// UTIL
  protected void swapItem(int id1, int id2){
    if(id1 != id2){
      ItemStack tmp = getItem(id2);
      setItem(id2, getItem(id1));
      setItem(id1, tmp);
    }
  }

  public AbstractAgentEntity getAgent(){return agent;}
}
