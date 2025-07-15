package com.github.ptran779.aegisops.block;

import com.github.ptran779.aegisops.server.BlockInit;
import com.github.ptran779.aegisops.server.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class HellPodBlockUsedTop extends Block {
  public HellPodBlockUsedTop(Properties pProperties) {
    super(pProperties);
  }

  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    super.onRemove(state, world, pos, newState, isMoving);

    if (!state.is(newState.getBlock())) {
      BlockPos below = pos.below();
      if (world.getBlockState(below).getBlock() == BlockInit.HELL_POD_USED_BOT.get()) {
        world.removeBlock(below, false);
      }
    }
  }
  public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
    return new ItemStack(ItemInit.HELL_POD_ITEM.get());
  }
}
