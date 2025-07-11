package com.github.ptran779.aegisops.entity.util;

import com.github.ptran779.aegisops.Config.SkinManager;
import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.goal.*;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.ptran779.aegisops.attribute.AgentAttribute.*;

public abstract class AbstractAgentEntity extends PathfinderMob implements MenuProvider, IEntityTeam, IEntityTarget {
  public String agentType = "Template";
  private boolean persistedFromNBT = false;

  //inventory slot
  public AgentInventory inventory = new AgentInventory(16, this);  // so I can handle inventory related stuff cleaner
  public final int[] gearSlots = {inventory.getContainerSize()-4,inventory.getContainerSize()-3,inventory.getContainerSize()-2,inventory.getContainerSize()-1};
  public final int gunSlot = inventory.getContainerSize()-7;
  public final int meleeSlot = inventory.getContainerSize()-6;
  public final int specialSlot = inventory.getContainerSize()-5;

  // agent custom config
  private UUID bossUUID = null;
  public UUID followPlayer = null;
  public int maxfood = 40;
  private int pathCooldown = 0;

  // auto sync variable. Useful for setting flag
  private static final EntityDataAccessor<Integer> MOVEMENT_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Boolean> ALLOW_SPECIAL_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Integer> AUTO_HOSTILE_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);
  public static final EntityDataAccessor<Boolean> KEEP_EAT_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
  public static final EntityDataAccessor<Integer> FOOD_VALUE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<String> OWNER = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.STRING);
  private static final EntityDataAccessor<Integer> VIRTUAL_AMMO = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);  // for render purpose

  private static final EntityDataAccessor<Integer> ANI_MOVE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);  // for render purpose
  public static final EntityDataAccessor<Boolean> FEMALE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
  public static final EntityDataAccessor<String> SKIN = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.STRING);
  //skin quick lookup
  private transient ResourceLocation cachedSkin;

  // animation -- client render only -- send packet to server to update when to play
  public float timeTrigger = -1000;

  public AbstractAgentEntity(EntityType<? extends AbstractAgentEntity> entityType, Level level) {
    super(entityType, level);
    ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
    this.getNavigation().setCanFloat(true);
    setPersistenceRequired();  // do not despawn agent
  }

  public ResourceLocation getResolvedSkin() {
    if (cachedSkin == null) {
      cachedSkin = SkinManager.get(this.entityData.get(FEMALE), this.entityData.get(SKIN));  // Lookup in your skin manager
    }
    return cachedSkin;
  }

  public static AttributeSupplier.@NotNull Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 20)
        .add(Attributes.MOVEMENT_SPEED, 0.5)
        .add(Attributes.JUMP_STRENGTH, 1)
        .add(Attributes.FOLLOW_RANGE, 16)
        .add(Attributes.ATTACK_DAMAGE, 1)
        .add(AGENT_ATTACK_SPEED, 1);
  }

  // extra sync data for client and server
  protected void defineSynchedData(){
    super.defineSynchedData();
    entityData.define(ALLOW_SPECIAL_F, false);
    entityData.define(MOVEMENT_F, 0);
    entityData.define(AUTO_HOSTILE_F, Utils.TargetMode.OFF.ordinal());
    entityData.define(KEEP_EAT_F, false);
    entityData.define(FOOD_VALUE, 0);
    entityData.define(ANI_MOVE, Utils.AniMove.NORM.ordinal());

    entityData.define(FEMALE, false);
    entityData.define(SKIN, "");
    entityData.define(OWNER, "");
    entityData.define(VIRTUAL_AMMO, 0);
  }

  public String getOwner() {return this.entityData.get(OWNER);}
  public void setOwner(String boss) {this.entityData.set(OWNER, boss);}

  public UUID getBossUUID() {return this.bossUUID;}
  public void setBossUUID(UUID uuid) {
    this.bossUUID = uuid;
    Player boss = this.level().getServer().getPlayerList().getPlayer(uuid);
    if (boss != null) {setOwner(boss.getGameProfile().getName());
    } else {setOwner("");}
  }

  public boolean getAllowSpecial() {return this.entityData.get(ALLOW_SPECIAL_F);}
  public void setAllowSpecial(boolean flag) {this.entityData.set(ALLOW_SPECIAL_F, flag);}
  public boolean getKeepEating() {return this.entityData.get(KEEP_EAT_F);}
  public void setKeepEating(boolean flag) {this.entityData.set(KEEP_EAT_F, flag);}
  //0: wander, 1: stand guard, 2: follow, 3(wip) patrol
  public int getMovement() {return this.entityData.get(MOVEMENT_F);}
  public void setMovement(int flag, UUID player) {
    this.entityData.set(MOVEMENT_F, flag);
    this.followPlayer = getMovement() == 2 ? player : null;
  }
  public Utils.TargetMode getTargetMode() {return Utils.TargetMode.fromId(this.entityData.get(AUTO_HOSTILE_F));}
  public void setTargetMode(Utils.TargetMode mode) {this.entityData.set(AUTO_HOSTILE_F, mode.ordinal());}
  public Utils.TargetMode nextTargetMode() {return Utils.TargetMode.nextTargetMode(this.entityData.get(AUTO_HOSTILE_F));}
  public boolean haveWeapon(){return this.inventory.haveWeapon();}

  public int getVirtualAmmo(){return this.entityData.get(VIRTUAL_AMMO);}
  public void setVirtualAmmo(int ammo){this.entityData.set(VIRTUAL_AMMO, ammo);};
  public Integer getFood() {return this.entityData.get(FOOD_VALUE);}
  public void setFood(Integer val) {this.entityData.set(FOOD_VALUE, val);}
  public boolean getFemale() {return this.entityData.get(FEMALE);}
  public void setFemale(boolean flag) {this.entityData.set(FEMALE, flag);}
  public Utils.AniMove getAniMove() {return Utils.AniMove.fromId(this.entityData.get(ANI_MOVE));}
  public void setAniMove(Utils.AniMove move) {this.entityData.set(ANI_MOVE, move.ordinal());}

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new FloatGoal(this));
    this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
    this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    this.goalSelector.addGoal(8, new OpenDoorGoal(this, true));
    this.goalSelector.addGoal(9, new CustomRandomStrollGoal(this, 0.4, 80));

    this.goalSelector.addGoal(3, new RechargeVirtualAmmo(this, 60));
    this.goalSelector.addGoal(4, new EatFoodGoal(this));  // I still need to reduce food value from action
    this.goalSelector.addGoal(5, new FollowGoal(this));
    this.goalSelector.addGoal(6, new Salute(this, 100));
  }

  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    if(!this.level().isClientSide()) {
      if (getBossUUID() == null) {
        setBossUUID(player.getUUID());}
      if (sameTeam(player)) {
        if (player.getMainHandItem().getItem() instanceof SwordItem) {
          Utils.TargetMode mode = nextTargetMode();
          setTargetMode(mode);
          String disp = switch (mode) {
            case OFF -> this.getName().getString() + " shall not seek battle";
            case HOSTILE_ONLY -> this.getName().getString() + " shall hunt down hostile";
            case ENEMY_AGENTS -> this.getName().getString() + " shall hunt down other players";
            case ALL -> this.getName().getString() + " shall hunt down hostile and other players";
          };
          player.displayClientMessage(Component.literal(disp), true);
        } else if (player.isCrouching()) {
          int i = this.getMovement() == 2 ? 0 : this.getMovement() + 1;
          this.setMovement(i, getBossUUID());
          String disp = switch (i) {
            case 0 -> this.getName().getString() + " will wander around here";
            case 1 -> this.getName().getString() + " will stand guard";
            case 2 -> {
              Entity target = ((ServerLevel) level()).getEntity(this.followPlayer);
              String name = target != null ? target.getName().getString() : "???";
              yield this.getName().getString() + " will follow " + name;
            }
            default -> "";
          };
          player.displayClientMessage(Component.literal(disp), true);
        } else {
          NetworkHooks.openScreen((ServerPlayer) player, this, buf -> buf.writeInt(this.getId()));
        }
      }
    }
    return InteractionResult.SUCCESS;
  }

  // check to make sure same owner, or owner in same team,
  private boolean sameTeam(LivingEntity entity) {
    if (entity instanceof Player player) {
      return isFriendlyPlayer(player);
    } else if (entity instanceof IEntityTeam teamer){
      return isFriendlyMod(teamer);
    }
    return false;
  }

  public boolean isFriendlyPlayer(Player player) {
    // Boss
    if (getBossUUID() == null) {return false;}
    if (getBossUUID().equals(player.getUUID())) return true;
    // Team
    return player.getTeam().getName().equals(this.level().getPlayerByUUID(getBossUUID()).getTeam().getName());  // same team
  }
  public boolean isFriendlyMod(IEntityTeam teamer) {
    // boss
    if (getBossUUID() == null || teamer.getBossUUID() == null) {return false;}
    if (getBossUUID().equals(teamer.getBossUUID())) {return true;}
    //team with boss
    return this.level().getPlayerByUUID(teamer.getBossUUID()).getTeam().getName().equals(this.level().getPlayerByUUID(getBossUUID()).getTeam().getName());
  }
  public boolean shouldTargetEntity(LivingEntity entity) {
    Utils.TargetMode mode = getTargetMode();
    boolean out = switch (mode) {
      case OFF -> false;
      case HOSTILE_ONLY -> entity instanceof Enemy;
      case ENEMY_AGENTS -> {
        if (entity instanceof Player player) yield !isFriendlyPlayer(player);
        if (entity instanceof IEntityTeam teamer) yield !isFriendlyMod(teamer);
        yield false;
      }
      case ALL -> {
        if (entity instanceof Enemy) yield true;
        if (entity instanceof Player player) yield !isFriendlyPlayer(player);
        if (entity instanceof IEntityTeam teamer) yield !isFriendlyMod(teamer);
        yield false;
      }
    };
    return out;
  }

  public boolean hurt(DamageSource source, float amount) {
    Entity entity = source.getEntity();
    if (entity != null) {
      if (entity instanceof LivingEntity living && !sameTeam(living)) {this.setTarget(living);}
    }
    return super.hurt(source, amount);
  }

  // damage calculation on armor, and auto equip new
  protected void hurtArmor(DamageSource pSource, float pDamage) {
    if (!(pDamage <= 0.0F)) {
      pDamage /= 4.0F;
      if (pDamage < 1.0F) {
        pDamage = 1.0F;
      }
      AtomicBoolean brokenArmorPiece = new AtomicBoolean(false);
      for(ItemStack piece : this.getArmorSlots()) {
        if ((!pSource.is(DamageTypeTags.IS_FIRE) || !piece.getItem().isFireResistant()) && piece.getItem() instanceof ArmorItem) {
          piece.hurtAndBreak((int)pDamage, this, (e) -> {
            e.broadcastBreakEvent(LivingEntity.getEquipmentSlotForItem(piece));
            brokenArmorPiece.set(true);
          });
        }
      }
      if (brokenArmorPiece.get()) {
        inventory.loadArmor();
        brokenArmorPiece.set(false);
      }
    }
  }

  public void addAdditionalSaveData(CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);
    // save inventory
    ListTag invTag = new ListTag();
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        CompoundTag itemTag = new CompoundTag();
        itemTag.putByte("Slot", (byte) i);
        stack.save(itemTag);
        invTag.add(itemTag);
      }
    }
    nbt.put("AgentInventory", invTag); // save entire inventory as one list

    // save other data
    nbt.putInt("Food", this.getFood());
    if (getBossUUID() != null) {nbt.putUUID("owner_uuid", getBossUUID());}
    nbt.putBoolean("allow_special", this.getAllowSpecial());
    nbt.putInt("auto_hostile", this.entityData.get(AUTO_HOSTILE_F));
    nbt.putBoolean("is_female", getFemale());
    nbt.putString("skin", this.entityData.get(SKIN));
//    nbt.putBoolean("attack_player", this.getAttackPlayer());
    nbt.putInt("movement", this.getMovement());
    nbt.putInt("virtual_ammo", this.getVirtualAmmo());
  }
  @Override
  public void readAdditionalSaveData(CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
    this.persistedFromNBT = true;
    // Load inventory
    ListTag invTag = nbt.getList("AgentInventory", Tag.TAG_COMPOUND);
    for (int i = 0; i < invTag.size(); i++) {
      CompoundTag itemTag = invTag.getCompound(i);
      int slot = itemTag.getByte("Slot") & 255;
      if (slot < this.inventory.getContainerSize()) {this.inventory.setItem(slot, ItemStack.of(itemTag));}
    }

    // load other data
    setFood(nbt.getInt("Food"));
    if (nbt.contains("owner_uuid")){setBossUUID(nbt.getUUID("owner_uuid"));}
    else {setBossUUID(null);}

    this.setAllowSpecial(nbt.getBoolean("allow_special"));
    this.setTargetMode(Utils.TargetMode.values()[nbt.getInt("auto_hostile")]);
    setFemale(nbt.getBoolean("is_female"));
    this.entityData.set(SKIN, nbt.getString("skin"));
//    this.setAttackPlayer(nbt.getBoolean("attack_player"));
    this.setMovement(nbt.getInt("movement"), this.getBossUUID());
    this.setVirtualAmmo(nbt.getInt("virtual_ammo"));
  }

  public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
    SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    this.setLeftHanded(false);  // everyone use right hand pls
    if (!persistedFromNBT) {
      boolean isFemale = ThreadLocalRandom.current().nextBoolean();
      this.setCustomName(Component.literal(Utils.randomName(isFemale)));
      setFemale(isFemale);
      this.entityData.set(SKIN, SkinManager.renerateRandom(isFemale));
    }
    return data;
  }

  public void initCosmetic(){
    boolean isFemale = ThreadLocalRandom.current().nextBoolean();
    this.setCustomName(Component.literal(Utils.randomName(isFemale)));
    setFemale(isFemale);
    this.entityData.set(SKIN, SkinManager.renerateRandom(isFemale));
  }

  @Override
  protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
    super.dropCustomDeathLoot(source, looting, recentlyHit);
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        this.spawnAtLocation(stack);
      }
    }
  }

  public void tick(){
    super.tick();
    if(!level().isClientSide() && tickCount % 80 == 0) {
      var attr = getAttribute(Attributes.MOVEMENT_SPEED);
      if (getFood() >= maxfood * 0.4) {
        if (attr != null && !attr.hasModifier(WELL_FEED_SPEED_BOOST)) {
          attr.addTransientModifier(WELL_FEED_SPEED_BOOST);
        }
      } else {
        // Remove if no longer well-fed
        if (attr != null && attr.hasModifier(WELL_FEED_SPEED_BOOST)) {
          attr.removeModifier(WELL_FEED_SPEED_BOOST);
        }
      }
      if (getHealth()<getMaxHealth() && getFood() >= maxfood * 0.25) {
        passiveRegen();
      }
    }
  }

  private void passiveRegen() {
    this.setFood(this.getFood() - 1);
    this.heal(1);
  }

  public boolean moveto(Entity pEntity, double pSpeed){
    if (--pathCooldown <= 0) {
      this.pathCooldown = 10;  // only compute every 20 tick
      return this.getNavigation().moveTo(pEntity, pSpeed);
    }
    return true;
  }

  public void stopNav(){
    this.getNavigation().stop();
    this.pathCooldown = 0;
  }

  //menu
  @Override
  public AbstractContainerMenu createMenu(int containerID, Inventory inventory, Player player) {
    return new AgentInventoryMenu(containerID, inventory, this);
  }

//  public void updateSwingTime(){super.updateSwingTime();}

  public void clearTarget() {setTarget(null);}

  //just stack to mainhand, perfrom check since it call draw, which take entity a few tick to process drawing
  public void equipGun() {
    if (getMainHandItem() != inventory.getItem(gunSlot)){
      setItemSlot(EquipmentSlot.MAINHAND, inventory.getItem(gunSlot));
      IGunOperator op = IGunOperator.fromLivingEntity(this);
      op.draw(this::getMainHandItem);
    }
  }
  // just stack to mainhand
  public void equipMelee() {setItemSlot(EquipmentSlot.MAINHAND, inventory.getItem(meleeSlot));}
  // special
  public void equipSpecial() {setItemSlot(EquipmentSlot.MAINHAND, inventory.getItem(specialSlot));}
  public ItemStack getSpecialSlot() {return inventory.getItem(specialSlot);}
  public ItemStack getGunSlot(){return inventory.getItem(gunSlot);}

  // for overwrite later in final class
  public boolean isEquipableGun(ItemStack stack) {return false;}
  public boolean isEquipableMelee(ItemStack stack) {return false;}
  public int getMaxVirtualAmmo(){return 0;}
  public int getAmmoPerCharge(){return 1;}
}
