package com.github.ptran779.aegisops.entity;

import com.github.ptran779.aegisops.Config.AgentConfig;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.tacz.guns.api.item.nbt.GunItemDataAccessor.GUN_ID_TAG;

public class Sniper extends AbstractAgentEntity {
    private static AgentConfig config;
    public Sniper(EntityType<? extends AbstractAgentEntity> entityType, Level level) {
        super(entityType, level);
        this.agentType = "Sniper";
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }

    public void tick() {
        super.tick();
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static void updateClassConfig(@Nonnull AgentConfig config) {
      Sniper.config = config;}

    @Override
    public boolean isEquipableGun(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        String gunId = nbt.getString(GUN_ID_TAG);
        if (gunId.isEmpty()) return false;
        return config.allowGuns.contains(gunId);
    }
}