package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.item.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            AegisOps.MOD_ID);

    public static final RegistryObject<ForgeSpawnEggItem> SOLDIER_SPAWN_EGG = ITEMS.register("soldier_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.SOLDIER,0xFFFFFF, 0xFFFFFF,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<ForgeSpawnEggItem> SNIPER_SPAWN_EGG = ITEMS.register("sniper_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.SNIPER,0xFFFFFF, 0xFFFFFF,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<ForgeSpawnEggItem> HEAVY_SPAWN_EGG = ITEMS.register("heavy_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.HEAVY,0xFFFFFF, 0xFFFFFF,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<ForgeSpawnEggItem> DEMOLITION_SPAWN_EGG = ITEMS.register("demolition_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.DEMOLITION,0xFFFFFF, 0xFFFFFF,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<ForgeSpawnEggItem> MEDIC_SPAWN_EGG = ITEMS.register("medic_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.MEDIC,0xFFFFFF, 0xFFFFFF,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<ForgeSpawnEggItem> ENGINEER_SPAWN_EGG = ITEMS.register("engineer_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.ENGINEER,0xFFFFFF, 0xFFFFFF,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<ForgeSpawnEggItem> SWORDMAN_SPAWN_EGG = ITEMS.register("swordman_spawn_egg",
        () -> new ForgeSpawnEggItem(EntityInit.SWORDMAN,0xFFFFFF, 0xFFFFFF,
            new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> DROP_POD_ITEM = ITEMS.register("drop_pod",
        () -> new DropPodItem(new Item.Properties()));

    public static final RegistryObject<Item> HELL_POD_ITEM = ITEMS.register("hell_pod",
        () -> new HellPodItem(new Item.Properties()));

    public static final RegistryObject<Item> BEACON_ITEM = ITEMS.register("beacon",
        () -> new BeaconItem(new Item.Properties()));

    public static final RegistryObject<Item> DB_TURRET_ITEM = ITEMS.register("db_turret",
        () -> new DBTurretItem(new Item.Properties()));

    public static final RegistryObject<Item> PORT_DISP_ITEM = ITEMS.register("port_disp",
        () -> new PortDispItem(new Item.Properties()));

    public static final RegistryObject<Item> ENGI_HAMMER_ITEM = ITEMS.register("engi_hammer",
        () -> new EngiHammerItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BANDAGE_ITEM = ITEMS.register("bandage",
        () -> new BandageItem(new Item.Properties()));
    public static final RegistryObject<Item> MORPHINE_ITEM = ITEMS.register("morphine",
        () -> new MorphineItem(new Item.Properties()));
    public static final RegistryObject<Item> MODULAR_SHIELD_ITEM = ITEMS.register("modular_shield",
        () -> new ModularShieldItem(new Item.Properties().durability(600)));
    public static final RegistryObject<Item> GRENADE_ITEM = ITEMS.register("grenade",
        () -> new GrenadeItem(new Item.Properties()));
    public static final RegistryObject<Item> VP_ITEM = ITEMS.register("vp_terminal",
        () -> new VPTerminalItem(new Item.Properties()));
}
