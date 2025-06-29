package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.block.BeaconBlock;
import com.github.ptran779.aegisops.block.BeaconBlockUnused;
import com.github.ptran779.aegisops.block.DropPodBlock;
import com.github.ptran779.aegisops.block.DropPodBlockUsed;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, AegisOps.MOD_ID);

  public static final RegistryObject<Block> DROP_POD = BLOCKS.register("drop_pod_block", () -> new DropPodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));
  public static final RegistryObject<Block> DROP_POD_USED = BLOCKS.register("drop_pod_block_used", () -> new DropPodBlockUsed(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));

  public static final RegistryObject<Block> BEACON = BLOCKS.register("beacon_block", () -> new BeaconBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));
  public static final RegistryObject<Block> BEACON_UNUSED = BLOCKS.register("beacon_block_unused", () -> new BeaconBlockUnused(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));
}
