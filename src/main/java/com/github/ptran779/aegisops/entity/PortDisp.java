package com.github.ptran779.aegisops.entity;

import com.github.ptran779.aegisops.Config.ServerConfig;
import com.github.ptran779.aegisops.entity.util.AbstractAgentStruct;
import com.github.ptran779.aegisops.item.EngiHammerItem;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.github.ptran779.aegisops.network.StructureRenderPacket;
import com.github.ptran779.aegisops.server.ItemInit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class PortDisp extends AbstractAgentStruct {
  // animation -- client render only -- send packet to server to update when to play
  public float timeTrigger = -200;
  public static final int STAY_OPEN = 200;
  public int lastInteract = 0;

  private static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(DBTurret.class, EntityDataSerializers.BOOLEAN);
  public PortDisp(EntityType<? extends Mob> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
  }

  public boolean getOpen() {return entityData.get(OPEN);}
  public void setOpen(boolean open) {
    boolean oldOpen = entityData.get(OPEN);
    entityData.set(OPEN, open);
    if (open) {
      lastInteract = 0;  // just a smaller and cleaner implementation
      if (!oldOpen) {
        PacketHandler.CHANNELS.send(
            PacketDistributor.TRACKING_ENTITY.with(() -> this),
            new StructureRenderPacket(this.getId(), 0.0f)
        );
      }
    } else {
      PacketHandler.CHANNELS.send(
          PacketDistributor.TRACKING_ENTITY.with(() -> this),
          new StructureRenderPacket(this.getId(), 0.0f)
      );
    }
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createLivingAttributes()
        .add(Attributes.MAX_HEALTH, 40.0)
        .add(Attributes.MOVEMENT_SPEED, 0.0)
        .add(Attributes.FOLLOW_RANGE, 16.0D);
  }
  protected void defineSynchedData(){
    super.defineSynchedData();
    entityData.define(OPEN, false);
  }
  public void addAdditionalSaveData(CompoundTag nbt) {super.addAdditionalSaveData(nbt);}
  public void readAdditionalSaveData(CompoundTag nbt) {super.readAdditionalSaveData(nbt);}
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    if (!level().isClientSide) {
      if (isFriendlyPlayer(player)) {
        if (player.getMainHandItem().getItem() instanceof EngiHammerItem) {
          this.spawnAtLocation(new ItemStack(ItemInit.PORT_DISP_ITEM.get()));
          ((ServerLevel) level()).sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), 5, 0, 2, 0, 0.02);
          level().playSound(null, blockPosition(), SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
          this.discard();
        } else {
          setOpen(true);
          player.displayClientMessage(Component.literal("Dispenser has " + charge + "/" + getMaxCharge() + " charge").withStyle(ChatFormatting.GOLD), true);
        }
      }
    }
    return InteractionResult.SUCCESS;
  }

  public void tick() {
    super.tick();
    if (!level().isClientSide()){
      if (lastInteract < STAY_OPEN) {
        if(++lastInteract >= STAY_OPEN){
          setOpen(false);
        };
      }
    }
  }

  public int getMaxCharge(){return ServerConfig.PORT_DIS_CHARGE_MAX.get();}
}
