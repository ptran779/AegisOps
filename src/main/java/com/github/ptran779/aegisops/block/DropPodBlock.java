package com.github.ptran779.aegisops.block;

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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DropPodBlock extends BaseEntityBlock {
  public static final VoxelShape SHAPE_OPEN = Shapes.or(Block.box(-16, 0, -16, 32, 8, 32),
      Block.box(-8, 8, -8, 24, 16, 24),
      Block.box(-4, 16, -4, -2, 48, 20),
      Block.box(18, 16, -4, 20, 48, 20),
      Block.box(-4, 16, 18, 20, 48, 20),
      Block.box(-4, 46, -4, 20, 48, 20));
  public static final VoxelShape SHAPE_CLOSE = Shapes.or(SHAPE_OPEN,
      Block.box(-4, 16, -4, 20, 48, -3));

  public static final BooleanProperty OPEN = BlockStateProperties.OPEN;


  public DropPodBlock(Properties pProperties) {
    super(pProperties);
    this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
    super.createBlockStateDefinition(pBuilder);
    pBuilder.add(OPEN);
  }

  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return state.getValue(OPEN) ? SHAPE_OPEN : SHAPE_CLOSE;
  }

  @Override
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
    super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
  }

  @Override
  public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
    if (!pLevel.isClientSide) {
      pLevel.setBlock(pPos, pState.setValue(OPEN, true), 10);

      if (pLevel.getBlockEntity(pPos) instanceof DropPodBE pod) {
        pod.openDoor = true;
        pod.setChanged();
        pLevel.sendBlockUpdated(pPos, pState, pState, 3);  // Send BE tag update
      }
    }

    return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return pLevel.isClientSide ? null : (lvl, pos, blkState, be) -> {
      if (be instanceof DropPodBE dropPod) {
        dropPod.tick();
      }
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState pState) {
    return RenderShape.INVISIBLE;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new DropPodBE(blockPos, blockState);
  }
}
