package com.github.ptran779.aegisops.entity.structure;

import com.github.ptran779.aegisops.entity.api.IEntityRender;
import com.github.ptran779.aegisops.entity.api.IEntityTeam;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.UUID;

public abstract class AbstractAgentStruct extends Mob implements IEntityTeam, IEntityRender {
  private UUID bossUUID;
  public int charge = 0;  // for engineer to use

  public AbstractAgentStruct(EntityType<? extends Mob> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
    setPersistenceRequired();  // do not despawn structure
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createLivingAttributes()
        .add(Attributes.MAX_HEALTH, 40.0)
        .add(Attributes.MOVEMENT_SPEED, 0.0)
        .add(Attributes.FOLLOW_RANGE, 16.0D);
  }

  public UUID getBossUUID(){return bossUUID;}
  public void setBossUUID(UUID bossID){this.bossUUID = bossID;}

  public boolean hurt(DamageSource source, float amount) {
    Entity entity = source.getEntity();
    if (entity != null) {
      if (entity instanceof LivingEntity living && !sameTeam(living)) {this.setTarget(living);}
    }
    if (level() instanceof ServerLevel serverLevel) {
      serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,getX(), getY() + 1, getZ(),4, 0.2, 0.2, 0.2, 0.01);
      serverLevel.sendParticles(ParticleTypes.SMOKE,getX(), getY() + 1, getZ(),4, 0.2, 0.2, 0.2, 0.01);
    }
    return super.hurt(source, amount);
  }

  // check to make sure same owner, or owner in same team,
  protected boolean sameTeam(LivingEntity entity) {
    if (entity instanceof Player player) {
      return isFriendlyPlayer(player, level());
    } else if (entity instanceof IEntityTeam teamer){
      return isFriendlyMod(teamer, level());
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

  public int getMaxCharge(){return 0;}  // overwrite me
}
