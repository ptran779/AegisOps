package com.github.ptran779.aegisops;

import com.github.ptran779.aegisops.Config.ServerConfig;
import com.github.ptran779.aegisops.server.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AegisOps.MOD_ID)
public class AegisOps {
    public static final String MOD_ID = "aegisops";

    public AegisOps() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(modEventBus);
        CreativeTabInit.CREATIVE_MODE_TABS.register(modEventBus);

        EntityInit.ENTITIES.register(modEventBus);
        MenuInit.MENU_TYPES.register(modEventBus);

        AttributeInit.ATTRIBUTES.register(modEventBus);
        EffectInit.EFFECTS.register(modEventBus);

        BlockInit.BLOCKS.register(modEventBus);
        BlockEntityInit.BLOCK_ENTITY.register(modEventBus);

        ServerConfig.register();
    }
}
