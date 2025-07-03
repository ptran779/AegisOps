package com.github.ptran779.aegisops.entity.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.UUID;

public class AbstractAgentStruct extends Mob implements IEntityTeam {
  private UUID bossUUID;
  public int charge = 0;  // for engineer to use

  public AbstractAgentStruct(EntityType<? extends Mob> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
    setPersistenceRequired();  // do not despawn structure
  }

  public UUID getBossUUID(){return bossUUID;}
  public void setBossUUID(UUID bossID){this.bossUUID = bossID;}

  public boolean hurt(DamageSource source, float amount) {
    Entity entity = source.getEntity();
    if (entity != null) {
      if (entity instanceof LivingEntity living && !sameTeam(living)) {this.setTarget(living);}
    }
    return super.hurt(source, amount);
  }

  // check to make sure same owner, or owner in same team,
  protected boolean sameTeam(LivingEntity entity) {
    if (entity instanceof Player player) {
      return isFriendlyPlayer(player);
    } else if (entity instanceof IEntityTeam teamer){
      return isFriendlyMod(teamer);
    }
    return false;
  }

  public Iterable<ItemStack> getArmorSlots() {return Collections.emptyList();}
  public ItemStack getItemBySlot(EquipmentSlot slot) {return ItemStack.EMPTY;}
  public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

  }
  public HumanoidArm getMainArm() {
    return null;
  }

  // no pushing turret around
  public boolean isPushable() {return false;}
  public void knockback(double strength, double xRatio, double zRatio) {}

  public void addAdditionalSaveData(CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);
    nbt.putInt("charge", charge);
    if (getBossUUID() != null) {nbt.putUUID("owner_uuid", getBossUUID());}
  }

  public void readAdditionalSaveData(CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
    charge = nbt.getInt("charge");
    if (nbt.contains("owner_uuid")){setBossUUID(nbt.getUUID("owner_uuid"));}
    else {setBossUUID(null);}
  }

  @Override
  public boolean isFriendlyPlayer(Player player) {
    // Boss
    if (getBossUUID() == null) {return false;}
    if (getBossUUID().equals(player.getUUID())) return true;
    // Team
    return player.getTeam().getName().equals(this.level().getPlayerByUUID(getBossUUID()).getTeam().getName());  // same team
  }

  @Override
  public boolean isFriendlyMod(IEntityTeam teamer) {
    // boss
    if (getBossUUID() == null || teamer.getBossUUID() == null) {return false;}
    if (getBossUUID().equals(teamer.getBossUUID())) {return true;}
    //team with boss
    return this.level().getPlayerByUUID(teamer.getBossUUID()).getTeam().getName().equals(this.level().getPlayerByUUID(getBossUUID()).getTeam().getName());
  }

  public int getMaxCharge(){return 0;}  // overwrite me
}
