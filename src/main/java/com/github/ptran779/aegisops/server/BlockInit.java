package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.block.*;
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

  public static final RegistryObject<Block> HELL_POD = BLOCKS.register("hell_pod_block", () -> new HellPodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));
  public static final RegistryObject<Block> HELL_POD_USED_BOT = BLOCKS.register("hell_pod_block_bot", () -> new HellPodBlockUsedBot(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));
  public static final RegistryObject<Block> HELL_POD_USED_TOP = BLOCKS.register("hell_pod_block_top", () -> new HellPodBlockUsedTop(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));

  public static final RegistryObject<Block> BEACON = BLOCKS.register("beacon_block", () -> new BeaconBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));
  public static final RegistryObject<Block> BEACON_UNUSED = BLOCKS.register("beacon_block_unused", () -> new BeaconBlockUnused(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));
}
