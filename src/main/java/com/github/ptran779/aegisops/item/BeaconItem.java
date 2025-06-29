package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.server.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BeaconItem extends BlockItem {

  public BeaconItem(Properties properties) {
    super(BlockInit.BEACON_UNUSED.get(), properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
    Player player = context.getPlayer();
    InteractionHand hand = context.getHand();

    BlockState state = BlockInit.BEACON_UNUSED.get().defaultBlockState();
    if (level.isEmptyBlock(pos)) {
      level.setBlock(pos, state, 3);
      if (player != null) {
        if (!player.isCreative()) {
          player.getItemInHand(hand).shrink(1);
        }
        level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
      }
      return InteractionResult.SUCCESS;
    }

    return InteractionResult.FAIL;
  }
}