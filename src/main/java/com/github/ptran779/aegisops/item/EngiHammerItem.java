package com.github.ptran779.aegisops.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EngiHammerItem extends Item {
  public EngiHammerItem(Properties pProperties) {
    super(pProperties);
  }
  public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
    pTooltipComponents.add(Component.literal("Engineer Special Item. Use to refill structure ammo"));
    super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
  }
}
