package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.entity.*;
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

  public static final RegistryObject<EntityType<Demolition>> DEMOLITION = ENTITIES.register("demolition", () -> EntityType.Builder.of(Demolition::new, MobCategory.MISC).build(new ResourceLocation(AegisOps.MOD_ID, "demolition").toString()));

  public static final RegistryObject<EntityType<Medic>> MEDIC = ENTITIES.register("medic", () -> EntityType.Builder.of(Medic::new, MobCategory.MISC).build(new ResourceLocation(AegisOps.MOD_ID, "medic").toString()));

  public static final RegistryObject<EntityType<Engineer>> ENGINEER = ENTITIES.register("engineer", () -> EntityType.Builder.of(Engineer::new, MobCategory.MISC).build(new ResourceLocation(AegisOps.MOD_ID, "engineer").toString()));

  public static final RegistryObject<EntityType<Swordman>> SWORDMAN = ENTITIES.register("swordman", () -> EntityType.Builder.of(Swordman::new, MobCategory.MISC).build(new ResourceLocation(AegisOps.MOD_ID, "swordman").toString()));

  public static final RegistryObject<EntityType<FallingDropPod>> FALLING_DROP_POD =
      ENTITIES.register("falling_drop_pod", () ->
          EntityType.Builder.of(FallingDropPod::new, MobCategory.MISC)
          .sized(1.0f, 1.5f)  // ← 1×1.5 block hit‐box so it’s clickable
          .build(new ResourceLocation(AegisOps.MOD_ID, "falling_drop_pod").toString()));

  public static final RegistryObject<EntityType<FallingHellPod>> FALLING_HELL_POD =
      ENTITIES.register("falling_hell_pod", () ->
          EntityType.Builder.of(FallingHellPod::new, MobCategory.MISC)
              .sized(1.0f, 1.5f)  // ← 1×1.5 block hit‐box so it’s clickable
              .build(new ResourceLocation(AegisOps.MOD_ID, "falling_hell_pod").toString()));

  public static final RegistryObject<EntityType<DBTurret>> BD_TURRET =
      ENTITIES.register("db_turret", () ->
          EntityType.Builder.of(DBTurret::new, MobCategory.MISC)
              .sized(1.0f, 1.8f)  // ← 1×1.5 block hit‐box so it’s clickable
              .build(new ResourceLocation(AegisOps.MOD_ID, "db_turret").toString()));
  public static final RegistryObject<EntityType<TurretBullet>> TURRET_BULLET = ENTITIES.register("turret_bullet",
      () -> EntityType.Builder.of(TurretBullet::new, MobCategory.MISC)
          .sized(0.125f, 0.125f)
          .clientTrackingRange(64)
          .updateInterval(1)
          .build("turret_bullet"));
  public static final RegistryObject<EntityType<PortDisp>> PORT_DISP =
      ENTITIES.register("port_disp", () ->
          EntityType.Builder.of(PortDisp::new, MobCategory.MISC)
              .sized(1.0f, 1f)  // ← 1×1.5 block hit‐box so it’s clickable
              .build(new ResourceLocation(AegisOps.MOD_ID, "port_disp").toString()));
}
