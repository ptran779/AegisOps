package com.github.ptran779.aegisops.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;

import static com.github.ptran779.aegisops.Utils.*;
import static com.github.ptran779.aegisops.server.BlockInit.DROP_POD;

public class FallingDropPod extends LivingEntity {
  private float xrand = 0f;
  private float zrand = 0f;


  public FallingDropPod(EntityType<? extends FallingDropPod> type, Level level) {
    super(type, level);
  }

  public void setDrift(float xrand, float zrand) {
    this.xrand = xrand;
    this.zrand = zrand;
  }

  public boolean isPickable() {return true;}

  @Override
  public void tick() {
    super.tick();
    // particle
    if (level().isClientSide) {
      level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,getX() + (random.nextDouble() - 0.5),getY(),getZ() + (random.nextDouble() - 0.5),0.0D, 0.1D, 0.0D); // small upward drift
      level().addParticle(ParticleTypes.CLOUD, getX()-0.5, getY(), getZ()-0.5 + (random.nextDouble() - 0.5),0.0D, 0.02D, 0.0D);
      level().addParticle(ParticleTypes.CLOUD, getX()+0.5, getY(), getZ()-0.5 + (random.nextDouble() - 0.5),0.0D, 0.02D, 0.0D);
      level().addParticle(ParticleTypes.CLOUD, getX()-0.5, getY(), getZ()+0.5 + (random.nextDouble() - 0.5),0.0D, 0.02D, 0.0D);
      level().addParticle(ParticleTypes.CLOUD, getX()+0.5, getY(), getZ()+0.5 + (random.nextDouble() - 0.5),0.0D, 0.02D, 0.0D);
    } else {
      // protect landed entity
      if (!this.getPassengers().isEmpty()) {
        for (Entity passenger : this.getPassengers()) {
          if (passenger instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 10, false, false));
          }
        }
      }
      Vec3 motion = this.getDeltaMovement();
      double downwardSpeed = Math.max(motion.y, -1);  // cap falling speed (-0.08 is default)
      this.setDeltaMovement(motion.x+xrand, downwardSpeed, motion.z+zrand);
      if (this.onGround()) {triggerCrash();}
    }
  }

  @Override
  public boolean canAddPassenger(Entity passenger) {
    return this.getPassengers().isEmpty(); // Only one passenger
  }

  @Override
  protected void positionRider(Entity passenger, MoveFunction moveFunc) {
    if (passenger != null && this.hasPassenger(passenger)) {
      moveFunc.accept(passenger, getX(), getY() + 1.0D, getZ());
    }
  }

  public InteractionResult interact(Player player, InteractionHand hand) {
    if (!level().isClientSide) {
      player.startRiding(this);
    }
    return InteractionResult.SUCCESS;
  }

  private void triggerCrash() {
    this.ejectPassengers();
    Level level = this.level();
    // Explosion
    ((ServerLevel) level).sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.5, getZ(), 20, 0.3, 0.3, 0.3, 0.05);
//    level.explode(this, getX(), getY(), getZ(), EXPLOSION_POWER.get().floatValue(), Level.ExplosionInteraction.TNT);
    level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.5F, 0.8F + random.nextFloat() * 0.4F);

    // Place block if possible
    BlockPos groundPos = findSolidGroundBelow(this.blockPosition(), level);
    if (groundPos != null) {
      for (int i = 0; i < 8; i++) {
        int dx = level.random.nextInt(5) - 2; // -2 to +2
        int dz = level.random.nextInt(5) - 2;
        BlockPos firePos = groundPos.offset(dx, 1, dz);

        if (level.getBlockState(firePos).isAir() &&
            level.getBlockState(firePos.below()).isSolidRender(level, firePos.below())) {
          level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 11);
        }
      }
      // Place pod block on solid ground
      level.setBlockAndUpdate(groundPos.above(), DROP_POD.get().defaultBlockState());
    }
    this.discard();
  }

  private static final EntityDataAccessor<Float> DATA_HEALTH = SynchedEntityData.defineId(FallingDropPod.class, EntityDataSerializers.FLOAT);

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_HEALTH, 100.0f); // must match max health
  }

  public static AttributeSupplier.Builder createAttributes() {
    return LivingEntity.createLivingAttributes()
        .add(Attributes.MAX_HEALTH, 100.0)
        .add(Attributes.MOVEMENT_SPEED, 0.0);
  }

  @Override
  public Iterable<ItemStack> getArmorSlots() {return Collections.emptyList();}

  @Override
  public ItemStack getItemBySlot(EquipmentSlot slot) {return ItemStack.EMPTY;}

  @Override
  public HumanoidArm getMainArm() {return null;}

  @Override
  public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {}
}

