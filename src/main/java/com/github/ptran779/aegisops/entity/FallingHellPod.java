package com.github.ptran779.aegisops.entity;

import com.github.ptran779.aegisops.entity.util.IEntityRender;
import com.github.ptran779.aegisops.network.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.github.ptran779.aegisops.server.BlockInit;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collections;

import static com.github.ptran779.aegisops.Utils.findSolidGroundBelow;
import static com.github.ptran779.aegisops.server.BlockInit.DROP_POD;

public class FallingHellPod extends LivingEntity implements IEntityRender {
  private float xrand = 0f;
  private float zrand = 0f;
  public float timeTrigger = 0;

  private static final EntityDataAccessor<Boolean> DEPLOYED = SynchedEntityData.defineId(DBTurret.class, EntityDataSerializers.BOOLEAN);

  public void setDrift(float xrand, float zrand) {
    this.xrand = xrand;
    this.zrand = zrand;
  }

  public FallingHellPod(EntityType<? extends FallingHellPod> type, Level level) {
    super(type, level);
  }

  public boolean isDeployed() {return entityData.get(DEPLOYED);}

  protected void defineSynchedData(){
    super.defineSynchedData();
    entityData.define(DEPLOYED, false);
  }
  public boolean isPickable() {return true;}

  @Override
  public void tick() {
    super.tick();
    // particle
    if (!level().isClientSide) {
      ((ServerLevel) level()).sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,getX() ,getY(),getZ(), 1,0,0,0, 0.01); // small upward drift
      Vec3 motion = this.getDeltaMovement();
      double downwardSpeed = Math.max(motion.y, -1);  // cap falling speed (-0.08 is default)
      if(isDeployed()) {
        if (tickCount-timeTrigger > 20){
          if (tickCount % 4 == 0) level().playSound(null, blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1F, 0.8F + random.nextFloat() * 0.4F);
          for (int i=0; i<2; i++) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME,getX() + 0.8 + i*0.3, getY()+2.4 - i*0.4, getZ(),
                1, 0, 0, 0.1, 0.01);
          }
          for (int i=0; i<2; i++) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME,getX() - 0.8 - i*0.3, getY()+2.4 - i*0.4, getZ(),
                1, 0, 0, 0.1, 0.01);
          }
          for (int i=0; i<2; i++) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME,getX(), getY()+2.4 - i*0.4, getZ() - 0.8 - i*0.3,
                1, 0.1, 0, 0, 0.01);
          }
          for (int i=0; i<2; i++) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME,getX(), getY()+2.4 - i*0.4, getZ() + 0.8 + i*0.3,
                1, 0.1, 0, 0, 0.01);
          }
          downwardSpeed = Math.max(motion.y, -0.5);
        }
      } else {
        BlockPos below = this.blockPosition();
        int groundY = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, below.getX(), below.getZ());
        int currentY = this.getBlockY();

        if (currentY - groundY <= 60) {
          entityData.set(DEPLOYED, true);
          PacketHandler.CHANNELS.send(
              PacketDistributor.TRACKING_ENTITY.with(() -> this),
              new EntityRenderPacket(this.getId(), 1)
          );
          resetRenderTick();
        }
      }

      if (!this.getPassengers().isEmpty()) {
        for (Entity passenger : this.getPassengers()) {
          if (passenger instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 10, false, false));
          }
        }
      }
      this.setDeltaMovement(motion.x+xrand, downwardSpeed, motion.z+zrand);
      if (this.onGround()) {
        triggerCrash();
      }
    }
  }

  private void triggerCrash() {
    Level level = this.level();
    ((ServerLevel) level).sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.5, getZ(), 20, 0.3, 0.3, 0.3, 0.05);
    level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1F, 0.8F + random.nextFloat() * 0.4F);
    level.playSound(null, blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1F, 0.8F + random.nextFloat() * 0.4F);

    BlockPos botPos = this.blockPosition().below();
    BlockPos bePos = botPos.below();

    // 1. Clear landing site
    level.setBlock(botPos, Blocks.AIR.defaultBlockState(), 3);

    // 2. Place HellPod BE base
    level.setBlock(bePos, BlockInit.HELL_POD.get().defaultBlockState(), 3);

    // 3. Transfer tagger or passengers (optional)
    if (!this.getPassengers().isEmpty()) {
      this.getPassengers().forEach(entity -> {
        entity.stopRiding();
        entity.teleportTo(bePos.getX() + 0.5, bePos.getY() + 0.1, bePos.getZ() + 0.5);
      });
    }
    this.discard(); // Remove falling entity
  }

  @Override
  public boolean canAddPassenger(Entity passenger) {
    return this.getPassengers().isEmpty(); // Only one passenger
  }

  @Override
  protected void positionRider(Entity passenger, MoveFunction moveFunc) {
    if (passenger != null && this.hasPassenger(passenger)) {
      moveFunc.accept(passenger, getX(), getY() + 0.5D, getZ());
    }
  }

  public InteractionResult interact(Player player, InteractionHand hand) {
    if (!level().isClientSide) {
      player.startRiding(this);
    }
    return InteractionResult.SUCCESS;
  }

//  private void triggerCrash() {
//    this.ejectPassengers();
//    Level level = this.level();
//    // Explosion
//    ((ServerLevel) level).sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.5, getZ(), 20, 0.3, 0.3, 0.3, 0.05);
////    level.explode(this, getX(), getY(), getZ(), EXPLOSION_POWER.get().floatValue(), Level.ExplosionInteraction.TNT);
//    level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.5F, 0.8F + random.nextFloat() * 0.4F);
//
//    // Place block if possible
//    BlockPos groundPos = findSolidGroundBelow(this.blockPosition(), level);
//    if (groundPos != null) {
//      for (int i = 0; i < 8; i++) {
//        int dx = level.random.nextInt(5) - 2; // -2 to +2
//        int dz = level.random.nextInt(5) - 2;
//        BlockPos firePos = groundPos.offset(dx, 1, dz);
//
//        if (level.getBlockState(firePos).isAir() &&
//            level.getBlockState(firePos.below()).isSolidRender(level, firePos.below())) {
//          level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 11);
//        }
//      }
//      // Place pod block on solid ground
//      level.setBlockAndUpdate(groundPos.above(), DROP_POD.get().defaultBlockState());
//    }
//    this.discard();
//  }

  public static AttributeSupplier.Builder createAttributes() {
    return LivingEntity.createLivingAttributes()
        .add(Attributes.MAX_HEALTH, 100.0)
        .add(Attributes.MOVEMENT_SPEED, 0.0);
  }

  public Iterable<ItemStack> getArmorSlots() {return Collections.emptyList();}
  public ItemStack getItemBySlot(EquipmentSlot slot) {return ItemStack.EMPTY;}
  public HumanoidArm getMainArm() {return null;}
  public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {}

  @Override
  public void resetRenderTick() {timeTrigger = tickCount;}
}

