package com.github.ptran779.aegisops.entity.agent;

import com.github.ptran779.aegisops.config.AgentConfig;
import com.github.ptran779.aegisops.brain.agent.TestBrain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Comparator;

import static com.tacz.guns.api.item.nbt.GunItemDataAccessor.GUN_ID_TAG;

public class Swordman extends AbstractAgentEntity {
    private static AgentConfig config;
    public Swordman(EntityType<? extends AbstractAgentEntity> entityType, Level level) {
        super(entityType, level);
        this.agentType = "Swordman";
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    protected void registerGoals() {
//        super.registerGoals();
//        this.goalSelector.addGoal(3, new CustomRetaliationTargetGoal(this));
//        this.goalSelector.addGoal(4, new CustomRangeTargetGoal<>(this, LivingEntity.class, 20, 20, 24, true, entity -> this.shouldTargetEntity(this, (LivingEntity) entity)));
//        this.goalSelector.addGoal(3, new AgentAttackGoal(this, 12, 12, 20));
    }
//    protected TestBrain agentBrain = new TestBrain(this);

    public void tick() {
        super.tick();
//        if (!level().isClientSide()) {
//            agentBrain.tick();
//        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        super.mobInteract(player, hand);
        if (!level().isClientSide) { // only run on server
          LivingEntity target = getTarget();
          if (target == null || !target.isAlive()){
              double radius = 80.0;
              AABB searchBox = this.getBoundingBox().inflate(radius, 4.0, radius);

              // Find nearest hostile manually
              LivingEntity nearest = level().getEntitiesOfClass(Monster.class, searchBox, e -> e.isAlive())
                  .stream()
                  .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                  .orElse(null);

              if (nearest != null) {
                // Assign to agent
                setTarget(nearest); // make sure AbstractAgentEntity has this
                player.sendSystemMessage(Component.literal("New target acquired: "
                    + nearest.getName().getString()
                    + " at " + nearest.blockPosition()));
            } else {
                player.sendSystemMessage(Component.literal("No hostile found nearby!"));
            }
          }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static void updateClassConfig(@Nonnull AgentConfig config) {
      Swordman.config = config;}

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