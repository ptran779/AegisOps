package com.github.ptran779.aegisops.block;

import com.github.ptran779.aegisops.server.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;


public class BeaconBlockUnused extends Block {
  public static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);
  public BeaconBlockUnused(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
    if (!pLevel.isClientSide) {
      BlockState newState = BlockInit.BEACON.get().defaultBlockState();
      pLevel.setBlock(pPos, newState, 3);
    }
    return InteractionResult.SUCCESS;
  }

  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }
}
