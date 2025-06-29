package com.github.ptran779.aegisops.block;

import com.github.ptran779.aegisops.server.ItemInit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.Level;


public class BeaconBlock extends BaseEntityBlock {
  public static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

  public BeaconBlock(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack tool) {
    super.playerDestroy(level, player, pos, state, be, tool);

    if (!level.isClientSide && be instanceof BeaconBE beacon) {
      if (beacon.step < 1060) {
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemInit.BEACON_ITEM.get()));
      }
    }
  }

  @Override
  public InteractionResult use(BlockState pState, @NotNull Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
    if (!pLevel.isClientSide) {
      if (pLevel.getBlockEntity(pPos) instanceof BeaconBE be) {
        if (be.step < 160) {
          pPlayer.displayClientMessage(Component.literal("Beacon is still charging...").withStyle(ChatFormatting.GOLD), true);
        } else if (be.step < 1060){
          pPlayer.displayClientMessage(Component.literal("Drop Pod arriving in " + (1060-be.step)/20.f + "s").withStyle(ChatFormatting.AQUA), true);
        } else {
          pPlayer.displayClientMessage(Component.literal("Drop Pod on route").withStyle(ChatFormatting.GREEN), true);
        }
      }
    }
    return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
  }

  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    return pLevel.isClientSide ? null : (lvl, pos, blkState, be) -> {
      if (be instanceof BeaconBE beacon) {
        beacon.tick();
      }
    };
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new BeaconBE(blockPos, blockState);
  }

  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }

  @Override
  public RenderShape getRenderShape(BlockState pState) {
    return RenderShape.INVISIBLE;
  }

  @Override
  public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
    super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
  }
}
