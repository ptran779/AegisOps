package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.client.ModClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ModularShieldItem extends ShieldItem {
  public ModularShieldItem(Properties pProperties) {
    super(pProperties);
  }

  @OnlyIn(Dist.CLIENT)
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    consumer.accept(new IClientItemExtensions() {
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return ModClientEvents.MODULAR_SHIELD_RENDER_INSTANCE; // Your renderer here
      }
    });
  }

  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    if (!level.isClientSide()) {
      ItemStack stack = player.getItemInHand(hand);
      CompoundTag tag = stack.getOrCreateTag();
      if (!player.isShiftKeyDown()) {
        if (!tag.contains("DeployTick")) {
          tag.putLong("DeployTick", level.getGameTime());
        }
      }
      else {tag.remove("DeployTick");}
    }
    // fallback: normal right-click
    return super.use(level, player, hand);
  }
  public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
    pTooltipComponents.add(Component.literal("Heavy Special Item, allow shield bash ability"));
    super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
  }
}
