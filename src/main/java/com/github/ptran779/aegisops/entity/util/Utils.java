package com.github.ptran779.aegisops.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import static com.github.ptran779.aegisops.server.EntityInit.*;

public class Utils {
  private static final List<EntityType<? extends AbstractAgentEntity>> AGENT_POOL = List.of(
      SOLDIER.get(),
      SNIPER.get(),
      HEAVY.get()
      // Add more if needed
  );

  public static AbstractAgentEntity getRandomAgent(Level level) {
    return AGENT_POOL.get(level.random.nextInt(AGENT_POOL.size())).create(level);
  }

  public static BlockPos findSolidGroundBelow(BlockPos start, Level level) {
    BlockPos.MutableBlockPos pos = start.mutable();

    while (pos.getY() > level.getMinBuildHeight()) {
      BlockState state = level.getBlockState(pos);
      if (!state.isAir()) {
        return pos.immutable(); // Found solid ground
      }
      pos.move(Direction.DOWN);
    }
    return null; // No ground found (shouldn't happen)
  }
}
