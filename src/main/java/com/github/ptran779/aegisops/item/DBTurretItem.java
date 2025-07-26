package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.entity.structure.DBTurret;
import com.github.ptran779.aegisops.server.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DBTurretItem extends Item {
  public DBTurretItem(Properties props) {
    super(props);
  }

  @Override
  public InteractionResult useOn(UseOnContext ctx) {
    if (ctx.getLevel().isClientSide) return InteractionResult.SUCCESS;

    Player player = ctx.getPlayer();
    Level level = ctx.getLevel();
    BlockPos spawnPos = ctx.getClickedPos().relative(ctx.getClickedFace());

    DBTurret turret = new DBTurret(EntityInit.BD_TURRET.get(), level);
    turret.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
    turret.setBossUUID(player.getUUID()); // your logic
    level.addFreshEntity(turret);

    ctx.getItemInHand().shrink(1);
    return InteractionResult.CONSUME;
  }
}
