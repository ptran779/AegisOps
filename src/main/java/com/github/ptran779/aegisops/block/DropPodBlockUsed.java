package com.github.ptran779.aegisops.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

// keep only for render. kill all ticking logic
public class DropPodBlockUsed extends BaseEntityBlock {
  public static final VoxelShape SHAPE_OPEN = Shapes.or(Block.box(-16, 0, -16, 32, 8, 32),
      Block.box(-8, 8, -8, 24, 16, 24),
      Block.box(-4, 16, -4, -2, 48, 20),
      Block.box(18, 16, -4, 20, 48, 20),
      Block.box(-4, 16, 18, 20, 48, 20),
      Block.box(-4, 46, -4, 20, 48, 20));

  public static final BooleanProperty OPEN = BlockStateProperties.OPEN;


  public DropPodBlockUsed(Properties pProperties) {
    super(pProperties);
    this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
    super.createBlockStateDefinition(pBuilder);
    pBuilder.add(OPEN);
  }

  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return SHAPE_OPEN;
  }

  @Override
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
    super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
  }

  @Override
  public RenderShape getRenderShape(BlockState pState) {
    return RenderShape.INVISIBLE;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new DropPodBE(blockPos, blockState, true);
  }
}
