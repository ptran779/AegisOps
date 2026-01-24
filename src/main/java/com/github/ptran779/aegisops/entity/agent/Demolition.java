package com.github.ptran779.aegisops.entity.agent;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.config.AgentConfig;
import com.github.ptran779.aegisops.goal.common.AgentAttackGoal;
import com.github.ptran779.aegisops.goal.common.CustomRangeTargetGoal;
import com.github.ptran779.aegisops.goal.common.CustomRetaliationTargetGoal;
import com.github.ptran779.aegisops.goal.special.DeployVPGoal;
import com.github.ptran779.aegisops.goal.special.ThrowGrenadeGoal;
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

public class Demolition extends AbstractAgentEntity {
    private static AgentConfig config;
    public Demolition(EntityType<? extends AbstractAgentEntity> entityType, Level level) {
        super(entityType, level);
        this.agentType = "Demolition";
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new DeployVPGoal(this, 100, 300));
        this.goalSelector.addGoal(2, new ThrowGrenadeGoal(this, 60, 300));
        this.goalSelector.addGoal(3, new CustomRetaliationTargetGoal(this));
        this.goalSelector.addGoal(4, new CustomRangeTargetGoal<>(this, LivingEntity.class, 30, 32, 48, true, entity -> this.shouldTargetEntity(this, (LivingEntity) entity)));
        this.goalSelector.addGoal(3, new AgentAttackGoal(this, 4, 8, 32));
    }

    public static void updateClassConfig(@Nonnull AgentConfig config) {Demolition.config = config;}
    public AgentConfig getAgentConfig() {return config;}
}