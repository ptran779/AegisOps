package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.block.DropPodBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, AegisOps.MOD_ID);

  public static final RegistryObject<Block> DROP_POD = BLOCKS.register("drop_pod", () -> new DropPodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F).noOcclusion()));

}
