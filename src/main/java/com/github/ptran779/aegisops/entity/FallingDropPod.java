package com.github.ptran779.aegisops.entity;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.github.ptran779.aegisops.entity.util.Utils.*;
import static com.github.ptran779.aegisops.server.BlockInit.DROP_POD;

public class FallingDropPod extends Entity {
  private boolean exploded = false;
  private final float xrand;
  private final float zrand;

  public FallingDropPod(EntityType<?> type, Level level) {
    super(type, level);
    xrand = level.random.nextFloat()*0.1f;
    zrand = level.random.nextFloat()*0.1f;
  }

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
    }

    // Constant downward motion
    this.setDeltaMovement(xrand, -0.5, zrand);  // need randomizer
    this.move(MoverType.SELF, this.getDeltaMovement());

    if (this.onGround() && !this.exploded) {
      triggerCrash();
    }
  }

  private void triggerCrash() {
    exploded = true;
    Level level = this.level();
    // Explosion
    level.explode(this, getX(), getY(), getZ(), 6F, Level.ExplosionInteraction.TNT);
    level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.5F, 0.8F + random.nextFloat() * 0.4F);

    // Place block if possible
    BlockPos groundPos = findSolidGroundBelow(this.blockPosition(), level);
    if (groundPos != null) {
      // clean landing area
      for (int dx = -2; dx <= 2; dx++) {
        for (int dz = -2; dz <= 2; dz++) {level.removeBlock(groundPos.offset(dx, 1, dz),false);}
      }
      // Place pod block on solid ground
      level.setBlockAndUpdate(groundPos.above(), DROP_POD.get().defaultBlockState());

      // Spawn agent on top
      AbstractAgentEntity agent = getRandomAgent(level);
      if (agent != null) {
        agent.setPos(groundPos.getX() + 0.5, groundPos.getY() + 2, groundPos.getZ() + 0.5);
        level.addFreshEntity(agent);
      }
    }
    this.discard();
  }

  @Override
  protected void defineSynchedData() {}
  @Override
  protected void readAdditionalSaveData(CompoundTag tag) {}
  @Override
  protected void addAdditionalSaveData(CompoundTag tag) {}
}

