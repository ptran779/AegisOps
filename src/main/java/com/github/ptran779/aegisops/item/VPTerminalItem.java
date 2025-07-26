package com.github.ptran779.aegisops.item;


import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.extra.VectorPursuer;
import com.github.ptran779.aegisops.server.EntityInit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VPTerminalItem extends Item {
  public VPTerminalItem(Properties pProperties) {
    super(pProperties);
  }

  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!level.isClientSide) {
      // Create the drone
      VectorPursuer drone = new VectorPursuer(EntityInit.VECTOR_PURSUER.get(), level);

      // Position the drone in front of the player
      Vec3 spawnPos = player.position().add(player.getLookAngle().scale(1.5));
      drone.setPos(spawnPos.x, spawnPos.y + 1, spawnPos.z);

      drone.deployerUUID = player.getUUID();
      drone.setTarget(Utils.findNearestEntity(player, Monster.class, 32, Entity::isAlive));  // clear me
      level.addFreshEntity(drone);

      // Optional: reduce item count
      if (!player.isCreative()) {
        stack.shrink(1);
      }
    }

    return InteractionResultHolder.consume(stack);
  }
}

