package com.github.ptran779.aegisops.entity.agent;

import com.github.ptran779.aegisops.config.AgentConfig;
import com.github.ptran779.aegisops.goal.common.AgentAttackGoal;
import com.github.ptran779.aegisops.goal.common.CustomRangeTargetGoal;
import com.github.ptran779.aegisops.goal.common.CustomRetaliationTargetGoal;
import com.github.ptran779.aegisops.goal.special.HypeUpGoal;
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

public class Soldier extends AbstractAgentEntity {
    private static AgentConfig config;
    public Soldier(EntityType<? extends AbstractAgentEntity> entityType, Level level) {
        super(entityType, level);
        this.agentType = "Soldier";
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new HypeUpGoal(this, 800));
        this.goalSelector.addGoal(3, new CustomRetaliationTargetGoal(this));
        this.goalSelector.addGoal(4, new CustomRangeTargetGoal<>(this, LivingEntity.class, 40, 32, 48, true, entity -> this.shouldTargetEntity(this, (LivingEntity) entity)));
        this.goalSelector.addGoal(3, new AgentAttackGoal(this, 4, 10, 32));
    }

    public static void updateClassConfig(@Nonnull AgentConfig config) {Soldier.config = config;}
    public AgentConfig getAgentConfig() {return config;}
}