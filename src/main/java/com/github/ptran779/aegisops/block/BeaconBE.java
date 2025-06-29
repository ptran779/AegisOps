package com.github.ptran779.aegisops.block;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.server.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import org.joml.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;

public class BeaconBE extends BlockEntity {
  public int step = 0;
  public boolean consumed = false;
  public static final int stop = 2000;  // should be 120, but need further for other thing
  private static final DustParticleOptions DUST_SHADE = new DustParticleOptions(new Vector3f(0.2f, 0.9f, 1.0f), 1.0f);


  public BeaconBE(BlockPos pPos, BlockState pBlockState) {
    super(BlockEntityInit.BEACON_BE.get(), pPos, pBlockState);
  }

  @Override
  protected void saveAdditional(CompoundTag pTag) {
    super.saveAdditional(pTag);
    pTag.putInt("aniStep", step);
  }

  @Override
  public void load(CompoundTag pTag) {
    super.load(pTag);
    step = pTag.getInt("aniStep");
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = super.getUpdateTag();
    tag.putInt("aniStep", this.step);
    return tag;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    super.handleUpdateTag(tag);
    this.step = tag.getInt("aniStep");
  }

  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    handleUpdateTag(pkt.getTag());
  }

  public void tick() {
    if (level != null && !level.isClientSide){
      if (step < stop) {
        step++;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
      }

      // Tick 1: soft hiss (fire extinguish / sponge dry)
      if (step == 1) {
        level.playSound(null, worldPosition, SoundEvents.ELDER_GUARDIAN_DEATH, SoundSource.BLOCKS, 1.2f, 1.0f);
        ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD,
            worldPosition.getX() + 0.5,
            worldPosition.getY() + 1.0,
            worldPosition.getZ() + 0.5,
            12, 0.25, 0.25, 0.25, 0.02);
      }
      // Tick 160: beacon pulse
      else if (step == 160) {
        level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.5f, 0.9f);
        ((ServerLevel) level).sendParticles(DUST_SHADE,
            worldPosition.getX() + 0.5,
            worldPosition.getY() + 1.1,
            worldPosition.getZ() + 0.5,
            20, 0.4, 0.2, 0.4, 0.05);
      }
      // Tick 1060: conduit-style surge
      else if (step == 1060) {
        level.playSound(null, worldPosition, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 1.4f, 1.0f);
        ((ServerLevel) level).sendParticles(ParticleTypes.ELECTRIC_SPARK,
            worldPosition.getX() + 0.5,
            worldPosition.getY() + 1.0,
            worldPosition.getZ() + 0.5,
            18, 0.3, 0.3, 0.3, 0.01);
        Utils.summonReinforcement(worldPosition.getX(),level.getMaxBuildHeight()-1,worldPosition.getZ(),(ServerLevel) level);
        consumed = true;
      } else if (step == 1260) {
        level.removeBlock(worldPosition, false); // remove block at this position, no drop
      }
    }
  }
}
