package com.github.ptran779.aegisops.entity.extra;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;

public class Grenade extends Entity {
  public static final float GRAVITY = -0.04f;
  protected int fuseTick=80;
  public boolean landed = false;
  public Grenade(EntityType<? extends Grenade> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
  }
  public void setFuseTick(int fuseTick) {this.fuseTick = fuseTick;}

  protected void defineSynchedData() {}
  protected void readAdditionalSaveData(CompoundTag compoundTag) {}
  protected void addAdditionalSaveData(CompoundTag compoundTag) {}

  public void tick() {
    super.tick();
    this.setOldPosAndRot();
    if (!this.onGround()) {
      this.setDeltaMovement(this.getDeltaMovement().add(0, GRAVITY, 0));
    } else {
      landed = true;
      this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.0, 0.5));
    }
    this.move(MoverType.SELF, this.getDeltaMovement());

    if (!level().isClientSide) {
      if (fuseTick-- <= 0) {
        level().explode(this, this.getX(), this.getY(), this.getZ(), 4.0F, Level.ExplosionInteraction.NONE);  // FIXME explosion power
        discard();
      }
    }
  }
}
