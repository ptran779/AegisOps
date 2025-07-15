package com.github.ptran779.aegisops.block;

import com.github.ptran779.aegisops.server.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HellPodBlock extends BaseEntityBlock {
  public static final VoxelShape SHAPE_OPEN = Shapes.or(Block.box(0, 0, 0, 16, 2, 16),
      Block.box(0, 0, 0, 1, 32, 16),
      Block.box(15, 0, 0, 16, 32, 16),
      Block.box(0, 0, 0, 16, 32, 1),
      Block.box(0, 0, 15, 16, 32, 16)
  );
  public static final VoxelShape SHAPE_CLOSE = Shapes.or(Block.box(0, 0, 0, 16, 32, 16));
  public static final BooleanProperty CLOSE = BooleanProperty.create("close");
  LivingEntity tagger;

  public HellPodBlock(Properties pProperties) {
    super(pProperties);
    this.registerDefaultState(this.stateDefinition.any().setValue(CLOSE, false));
  }

  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
    super.createBlockStateDefinition(pBuilder);
    pBuilder.add(CLOSE);
  }

  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return state.getValue(CLOSE)? SHAPE_CLOSE : SHAPE_OPEN;
  }
  public RenderShape getRenderShape(BlockState pState) {
    return RenderShape.INVISIBLE;
  }
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return type == BlockEntityInit.HELL_POD_BE.get() ?
        (lvl, pos, blkState, be) -> ((HellPodBE) be).tick() : null;
  }
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new HellPodBE(blockPos, blockState);
  }
}
