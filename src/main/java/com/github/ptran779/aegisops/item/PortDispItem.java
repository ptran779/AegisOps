package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.entity.DBTurret;
import com.github.ptran779.aegisops.entity.PortDisp;
import com.github.ptran779.aegisops.server.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class PortDispItem extends Item {
  public PortDispItem(Properties props) {
    super(props);
  }

  @Override
  public InteractionResult useOn(UseOnContext ctx) {
    if (ctx.getLevel().isClientSide) return InteractionResult.SUCCESS;

    Player player = ctx.getPlayer();
    Level level = ctx.getLevel();
    BlockPos spawnPos = ctx.getClickedPos().relative(ctx.getClickedFace());

    PortDisp portDisp = new PortDisp(EntityInit.PORT_DISP.get(), level);
    portDisp.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
    portDisp.setBossUUID(player.getUUID()); // your logic
    level.addFreshEntity(portDisp);

    ctx.getItemInHand().shrink(1);
    return InteractionResult.CONSUME;
  }
}
