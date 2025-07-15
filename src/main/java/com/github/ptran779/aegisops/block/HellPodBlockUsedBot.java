package com.github.ptran779.aegisops.block;

import com.github.ptran779.aegisops.item.HellPodItem;
import com.github.ptran779.aegisops.server.BlockInit;
import com.github.ptran779.aegisops.server.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class HellPodBlockUsedBot extends Block {
  public HellPodBlockUsedBot(Properties pProperties) {
    super(pProperties);
  }

  public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onPlace(state, world, pos, oldState, isMoving);

    BlockPos above = pos.above();
    BlockState topState = BlockInit.HELL_POD_USED_TOP.get().defaultBlockState();
    world.setBlock(above, topState, Block.UPDATE_ALL);
  }
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    super.onRemove(state, world, pos, newState, isMoving);

    if (!state.is(newState.getBlock())) {
      BlockPos above = pos.above();
      if (world.getBlockState(above).getBlock() == BlockInit.HELL_POD_USED_TOP.get()) {
        world.removeBlock(above, false);
      }
    }
  }
  public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
    return new ItemStack(ItemInit.HELL_POD_ITEM.get());
  }
}
