package com.github.ptran779.aegisops.entity;

import com.github.ptran779.aegisops.Config.AgentConfig;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.github.ptran779.aegisops.goal.AgentAttackGoal;
import com.github.ptran779.aegisops.goal.CustomRangeTargetGoal;
import com.github.ptran779.aegisops.goal.CustomRetaliationTargetGoal;
import com.github.ptran779.aegisops.goal.PrecisionShot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
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

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new PrecisionShot(this, 64, 600));
        this.goalSelector.addGoal(3, new CustomRetaliationTargetGoal(this));
        this.goalSelector.addGoal(4, new CustomRangeTargetGoal<>(this, LivingEntity.class, 60, 48, true, entity -> this.shouldTargetEntity((LivingEntity) entity)));
        this.goalSelector.addGoal(3, new AgentAttackGoal(this, 4, 16, 48, 48));
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
    public boolean isEquipableMelee(ItemStack stack) {
        return config.allowMelees.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
    }
    public int getMaxVirtualAmmo(){return config.maxVirtualAmmo;}
    public int getAmmoPerCharge(){return config.chargePerAmmo;}
}