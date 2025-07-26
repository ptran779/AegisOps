package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.entity.inventory.AgentInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public final class MenuInit {
  public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AegisOps.MOD_ID);

  public static final RegistryObject<MenuType<AgentInventoryMenu>> AEGISOPS_MENU = MENU_TYPES.register("aegisops_menu", () -> IForgeMenuType.create(AgentInventoryMenu::new));
}