package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.client.ModClientEvents;
import com.github.ptran779.aegisops.entity.extra.Grenade;
import com.github.ptran779.aegisops.server.EntityInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;


public class GrenadeItem extends Item {
  public static final double MaxThrowSpeed = 1.15;
  public GrenadeItem(Properties pProperties) {
    super(pProperties);
  }
  @OnlyIn(Dist.CLIENT)
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    consumer.accept(new IClientItemExtensions() {
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return ModClientEvents.GRENADE_RENDER_INSTANCE; // Your renderer here
      }
    });
  }

  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    CompoundTag tag = stack.getOrCreateTag();

    if (!tag.contains("DeployTick")) {
      if (!level.isClientSide()) {tag.putLong("DeployTick", level.getGameTime());}
    } else {player.startUsingItem(hand);}
    return InteractionResultHolder.consume(stack);
  }

  public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft) {
    if (!level.isClientSide() && stack.getOrCreateTag().contains("DeployTick")) {
      // Create grenade entity
      Grenade grenade = new Grenade(EntityInit.GRENADE.get(), level);
      grenade.setFuseTick(100);
      grenade.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());

      // Set velocity toward look direction
      Vec3 look = user.getLookAngle(); // already normalized
      grenade.setDeltaMovement(look.scale(MaxThrowSpeed));

      // Optional: set rotation to match
      grenade.setYRot(user.getYRot());
      grenade.setXRot(user.getXRot());

      // Spawn and play sound
      level.addFreshEntity(grenade);
      level.playSound(null, user.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 1.0F);

      // Clear tag so it doesn't repeat
      stack.removeTagKey("DeployTick");
    }
  }

  public UseAnim getUseAnimation(ItemStack stack) {return UseAnim.SPEAR;}
  public int getUseDuration(ItemStack stack) {return 72000;}
}

