package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabInit {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AegisOps.MOD_ID);

  public static final RegistryObject<CreativeModeTab> AEGIS_TAB =
    CREATIVE_MODE_TABS.register("aegis_tab", () -> CreativeModeTab.builder()
      .title(Component.translatable("itemGroup.aegisops.tab"))
      .icon(() -> new ItemStack(ItemInit.DROP_POD_ITEM.get()))
      .displayItems((params, output) -> {
        output.accept(ItemInit.SOLDIER_SPAWN_EGG.get());
        output.accept(ItemInit.SNIPER_SPAWN_EGG.get());
        output.accept(ItemInit.HEAVY_SPAWN_EGG.get());
        output.accept(ItemInit.DEMOLITION_SPAWN_EGG.get());
        output.accept(ItemInit.MEDIC_SPAWN_EGG.get());
        output.accept(ItemInit.ENGINEER_SPAWN_EGG.get());
        output.accept(ItemInit.SWORDMAN_SPAWN_EGG.get());
        output.accept(ItemInit.DROP_POD_ITEM.get());
        output.accept(ItemInit.HELL_POD_ITEM.get());
        output.accept(ItemInit.BEACON_ITEM.get());
        output.accept(ItemInit.DB_TURRET_ITEM.get());
        output.accept(ItemInit.PORT_DISP_ITEM.get());
        output.accept(ItemInit.ENGI_HAMMER_ITEM.get());
        output.accept(ItemInit.BANDAGE_ITEM.get());
        output.accept(ItemInit.MORPHINE_ITEM.get());
        output.accept(ItemInit.MODULAR_SHIELD_ITEM.get());
        output.accept(ItemInit.GRENADE_ITEM.get());
        output.accept(ItemInit.VP_ITEM.get());
        output.accept(ItemInit.BRAIN_CHIP_ITEM.get());
        // Add more if needed
      }).build()
    );
}
