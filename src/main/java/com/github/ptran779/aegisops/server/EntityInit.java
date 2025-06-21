package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.entity.FallingDropPod;
import com.github.ptran779.aegisops.entity.Heavy;
import com.github.ptran779.aegisops.entity.Soldier;
import com.github.ptran779.aegisops.entity.Sniper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class EntityInit {
  public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, AegisOps.MOD_ID);

  public static final RegistryObject<EntityType<Soldier>> SOLDIER = ENTITIES.register("soldier", () -> EntityType.Builder.of(Soldier::new, MobCategory.MISC).build(new ResourceLocation(AegisOps.MOD_ID, "soldier").toString()));
  public static final RegistryObject<EntityType<Sniper>> SNIPER = ENTITIES.register("sniper", () -> EntityType.Builder.of(Sniper::new, MobCategory.MISC).build(new ResourceLocation(AegisOps.MOD_ID, "sniper").toString()));
  public static final RegistryObject<EntityType<Heavy>> HEAVY = ENTITIES.register("heavy", () -> EntityType.Builder.of(Heavy::new, MobCategory.MISC).build(new ResourceLocation(AegisOps.MOD_ID, "heavy").toString()));

  public static final RegistryObject<EntityType<FallingDropPod>> FALLING_DROP_POD = ENTITIES.register("falling_drop_pod", () -> EntityType.Builder.of(FallingDropPod::new, MobCategory.MISC).build(new ResourceLocation(AegisOps.MOD_ID, "falling_drop_pod").toString()));
}
