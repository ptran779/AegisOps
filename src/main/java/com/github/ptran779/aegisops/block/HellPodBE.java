package com.github.ptran779.aegisops.block;

import com.github.ptran779.aegisops.server.BlockEntityInit;
import com.github.ptran779.aegisops.server.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HellPodBE extends BlockEntity {
  public int stepCounter = -40;
  public HellPodBE(BlockPos pPos, BlockState pBlockState) {
    super(BlockEntityInit.HELL_POD_BE.get(), pPos, pBlockState);
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = super.getUpdateTag();
    tag.putInt("stepCounter", stepCounter);
    return tag;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    super.handleUpdateTag(tag);
    stepCounter = tag.getInt("stepCounter");
  }

  public void tick() {
    stepCounter++;
    if (!level.isClientSide()) {
      if (stepCounter == 0) {
        ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD,
            worldPosition.getX(), worldPosition.getY() + 2.0, worldPosition.getZ(),
            10, 0.25, 0.25, 0.25, 0.02);
        level.playSound(null, worldPosition, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 1.0f, 1.0f);
      } else if(stepCounter == 10) {
        level.playSound(null, worldPosition, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
      } else if (stepCounter == 40) {
        level.playSound(null, worldPosition, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 1.0f, 1.0f);
      } else if (stepCounter == 30) {
        BlockState current = level.getBlockState(worldPosition);
        level.setBlock(worldPosition, current.setValue(HellPodBlock.CLOSE, true), 3);
      } else if (stepCounter >= 50) {
        level.setBlock(worldPosition, BlockInit.HELL_POD_USED_BOT.get().defaultBlockState(), 3);
        level.removeBlockEntity(worldPosition);
      }

      if(stepCounter >= 10 && stepCounter <= 30) {
        double minX = worldPosition.getX() - 0.5;
        double maxX = worldPosition.getX() + 0.5;
        double minY = worldPosition.getY();
        double maxY = worldPosition.getY() + Math.min((stepCounter - 10) * 0.1, 2); // full drop pod interior
        double minZ = worldPosition.getZ() + - 0.5;
        double maxZ = worldPosition.getZ() + 0.5;

        level.getEntities(null, new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ))
            .forEach(entity -> {
              entity.hurtMarked = true;
              if (stepCounter <= 30){
                entity.setDeltaMovement(0, 0.2, 0);
              } else {
                entity.teleportTo(entity.getX(), maxY, entity.getZ());
              }
            });
      }
    }
  }
}
