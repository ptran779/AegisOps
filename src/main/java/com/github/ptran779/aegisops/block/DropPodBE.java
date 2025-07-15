package com.github.ptran779.aegisops.block;

import com.github.ptran779.aegisops.server.BlockEntityInit;
import com.github.ptran779.aegisops.server.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.github.ptran779.aegisops.Config.ServerConfig.DROP_POD_DELAY_OPEN;

//Logic go here
public class DropPodBE extends BlockEntity{
  public boolean openDoor = false;
  public int openStep = 0;
  public int delayOpenTick = 0;
  public static final int doorOpenTime = 40;

  public DropPodBE(BlockPos pPos, BlockState pBlockState, boolean forceOpen) {
    super(BlockEntityInit.DROP_POD_BE.get(), pPos, pBlockState);
    if (forceOpen) {
      openDoor = true;
      openStep = doorOpenTime;
    }
  }

  public DropPodBE(BlockPos pPos, BlockState pBlockState) {
    super(BlockEntityInit.DROP_POD_BE.get(), pPos, pBlockState);
  }

  @Override
  protected void saveAdditional(CompoundTag pTag) {
    super.saveAdditional(pTag);
    pTag.putBoolean("openDoor", openDoor);
    pTag.putInt("openStep", openStep);
  }

  @Override
  public void load(CompoundTag pTag) {
    super.load(pTag);
    openDoor = pTag.getBoolean("openDoor");
    openStep = pTag.getInt("openStep");
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = super.getUpdateTag();
    tag.putInt("OpenStep", openStep);
    return tag;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    super.handleUpdateTag(tag);
    if (tag.contains("OpenStep")) {
      this.openStep = tag.getInt("OpenStep");
    }
  }
  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    handleUpdateTag(pkt.getTag());
  }

  public void tick() {//FIXME reoptimized to redude sync
    if (level != null && !level.isClientSide){
      if (DROP_POD_DELAY_OPEN.get() > -1 && ++delayOpenTick >= DROP_POD_DELAY_OPEN.get()) openDoor = true;

      if (openDoor) {
        if (openStep < doorOpenTime){
          openStep++;
          level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        } else {
          BlockState newState = BlockInit.DROP_POD_USED.get().defaultBlockState();
          level.setBlockAndUpdate(worldPosition, newState);
        }
      }
    }
  }
}
