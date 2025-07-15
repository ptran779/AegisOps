package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.block.BeaconBE;
import com.github.ptran779.aegisops.block.DropPodBE;
import com.github.ptran779.aegisops.block.HellPodBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AegisOps.MOD_ID);

  public static final RegistryObject<BlockEntityType<DropPodBE>> DROP_POD_BE =
      BLOCK_ENTITY.register("drop_pod_be", () ->
          BlockEntityType.Builder
              .of(DropPodBE::new,
                  BlockInit.DROP_POD.get(),
                  BlockInit.DROP_POD_USED.get() // add more if needed
              ).build(null));

  public static final RegistryObject<BlockEntityType<HellPodBE>> HELL_POD_BE =
      BLOCK_ENTITY.register("hell_pod_be", () ->
          BlockEntityType.Builder
              .of(HellPodBE::new,
                  BlockInit.HELL_POD.get()
              ).build(null));

  public static final RegistryObject<BlockEntityType<BeaconBE>> BEACON_BE =
      BLOCK_ENTITY.register("beacon_be", () ->
          BlockEntityType.Builder
              .of(BeaconBE::new,
                  BlockInit.BEACON.get()
//                  BlockInit.DROP_POD_USED.get() // add more if needed
              ).build(null));
}
