package com.github.ptran779.aegisops;

import com.github.ptran779.aegisops.server.*;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.github.ptran779.aegisops.server.ItemInit.*;

@Mod(AegisOps.MOD_ID)
public class AegisOps {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "aegisops";

    public AegisOps() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(modEventBus);
        CreativeTabInit.CREATIVE_MODE_TABS.register(modEventBus);

        EntityInit.ENTITIES.register(modEventBus);
        MenuInit.MENU_TYPES.register(modEventBus);

        AttributeInit.ATTRIBUTES.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        BlockEntityInit.BLOCK_ENTITY.register(modEventBus);

//        modEventBus.addListener(this::buildContents);
        Config.register();
    }

//    public void buildContents(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
//            event.accept(SOLDIER_SPAWN_EGG);
//            event.accept(SNIPER_SPAWN_EGG);
//        }
//    }
}
