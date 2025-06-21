package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.item.DropPodItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            AegisOps.MOD_ID);

    public static final RegistryObject<ForgeSpawnEggItem> SOLDIER_SPAWN_EGG = ITEMS.register("soldier_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.SOLDIER,0xE8AF5A, 0xFFFF00,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<ForgeSpawnEggItem> SNIPER_SPAWN_EGG = ITEMS.register("sniper_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.SOLDIER,0xE8AF5A, 0xFFFF00,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> DROP_POD_ITEM = ITEMS.register("drop_pod",
        () -> new DropPodItem(new Item.Properties()));
}
