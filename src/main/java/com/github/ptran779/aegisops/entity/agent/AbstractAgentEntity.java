package com.github.ptran779.aegisops.entity.agent;

import com.github.ptran779.aegisops.config.AgentConfig;
import com.github.ptran779.aegisops.config.SkinManager;
import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.inventory.AgentInventory;
import com.github.ptran779.aegisops.entity.inventory.AgentInventoryMenu;
import com.github.ptran779.aegisops.entity.api.IEntityRender;
import com.github.ptran779.aegisops.entity.api.IEntityTarget;
import com.github.ptran779.aegisops.entity.api.IEntityTeam;
import com.github.ptran779.aegisops.goal.common.*;
import com.github.ptran779.aegisops.goal.special.RechargeVirtualAmmo;
import com.github.ptran779.aegisops.network.render.EntityRenderPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.ptran779.aegisops.attribute.AgentAttribute.*;
import static com.tacz.guns.api.item.nbt.GunItemDataAccessor.GUN_ID_TAG;

public abstract class AbstractAgentEntity extends PathfinderMob implements InventoryCarrier, MenuProvider, IEntityTeam, IEntityTarget, IEntityRender {
  public String agentType = "Template";
  private boolean persistedFromNBT = false;
  public boolean invincible = false;
  public IGunOperator op;

  //inventory slot
  public AgentInventory inventory = new AgentInventory(16, this);  // so I can handle inventory related stuff cleaner
  public final int[] gearSlots = {0,1,2,3};
  public final int gunSlot = 4;
  public final int meleeSlot = 5;
  public final int specialSlot = 6;

  // agent custom config
//  private UUID bossUUID = null;
  public UUID followPlayer = null;
  public int maxfood = 40;
  private int pathCooldown = 0;

  // auto sync variable. Useful for setting flag
  private static final EntityDataAccessor<Integer> MOVEMENT_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Boolean> ALLOW_SPECIAL_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Integer> AUTO_HOSTILE_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);
  public static final EntityDataAccessor<Boolean> KEEP_EAT_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
  public static final EntityDataAccessor<Integer> FOOD_VALUE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> VIRTUAL_AMMO = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);  // for render purpose

  private static final EntityDataAccessor<Optional<UUID>> BOSS_UUID = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.OPTIONAL_UUID);
  private static final EntityDataAccessor<Integer> ANI_MOVE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);  // for render purpose
  private static final EntityDataAccessor<Integer> SPECIAL_MOVE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);  // for render purpose // there are a lot of these
  public static final EntityDataAccessor<Boolean> FEMALE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
  public static final EntityDataAccessor<String> SKIN = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.STRING);
  //skin quick lookup
  private transient ResourceLocation cachedSkin;

  // animation -- client render only -- send packet to server to update when to play
  public float timeTrigger = -1000;
  public void resetRenderTick() {timeTrigger = tickCount;}

  public AbstractAgentEntity(EntityType<? extends AbstractAgentEntity> entityType, Level level) {
    super(entityType, level);
    ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
    this.getNavigation().setCanFloat(true);
    setPersistenceRequired();  // do not despawn agent
    this.op = IGunOperator.fromLivingEntity(this);  // for gun
    this.setCanPickUpLoot(true);
  }

  public void flushSkinCache() {cachedSkin = null;}  // flush for render

  public ResourceLocation getResolvedSkin() {  // cache this for render. so O1 instead of hashmap
    if (cachedSkin == null) {cachedSkin = SkinManager.get(getFemale(), getSkin());}
    return cachedSkin;
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 20)
        .add(Attributes.MOVEMENT_SPEED, 0.5)
        .add(Attributes.JUMP_STRENGTH, 1)
        .add(Attributes.FOLLOW_RANGE, 16)
        .add(Attributes.ATTACK_DAMAGE, 1)
        .add(AGENT_ATTACK_SPEED, 1);
  }
  protected void defineSynchedData(){
    super.defineSynchedData();
    entityData.define(ALLOW_SPECIAL_F, false);
    entityData.define(MOVEMENT_F, 0);
    entityData.define(AUTO_HOSTILE_F, Utils.TargetMode.OFF.ordinal());
    entityData.define(KEEP_EAT_F, false);
    entityData.define(FOOD_VALUE, 0);
    entityData.define(ANI_MOVE, Utils.AniMove.NORM.ordinal());
    entityData.define(SPECIAL_MOVE, 0);

    entityData.define(FEMALE, false);
    entityData.define(SKIN, "");
    entityData.define(BOSS_UUID, Optional.empty());
    entityData.define(VIRTUAL_AMMO, 0);
  }

  public String getOwner() {
    UUID bossUUID = getBossUUID();
    if (bossUUID == null) return "";
    Player boss = this.level().getPlayerByUUID(bossUUID);
    if (boss == null) return "";
    return boss.getGameProfile().getName();
  }

  public UUID getBossUUID() {
    return this.entityData.get(BOSS_UUID).orElse(null);
  }
  public void setBossUUID(UUID uuid) {
    if (uuid == null) {this.entityData.set(BOSS_UUID, Optional.empty());}
    else {this.entityData.set(BOSS_UUID, Optional.of(uuid));}
  }

  public boolean getAllowSpecial() {return this.entityData.get(ALLOW_SPECIAL_F);}
  public void setAllowSpecial(boolean flag) {this.entityData.set(ALLOW_SPECIAL_F, flag);}
  public boolean getKeepEating() {return this.entityData.get(KEEP_EAT_F);}
  public void setKeepEating(boolean flag) {this.entityData.set(KEEP_EAT_F, flag);}
  //0: wander, 1: stand guard, 2: follow, 3(wip) patrol
//  public int getMovement() {return ;}  /// fix me you half bake code
  public Utils.FollowMode getFollowMode() {return Utils.FollowMode.fromId(this.entityData.get(MOVEMENT_F));}
  public Utils.FollowMode nextFollowMode() {return Utils.FollowMode.nextFollowMode(this.entityData.get(MOVEMENT_F));}
  public void setFollowMode(Utils.FollowMode mode, UUID player) {
    this.entityData.set(MOVEMENT_F, mode.ordinal());
    this.followPlayer = getFollowMode() == Utils.FollowMode.FOLLOW ? player : null;
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
  public String getSkin() {return this.entityData.get(SKIN);}
  public void setSkin(String skin) {this.entityData.set(SKIN, skin);}
  public Utils.AniMove getAniMove() {return Utils.AniMove.fromId(this.entityData.get(ANI_MOVE));}
  public void setAniMove(Utils.AniMove move) {this.entityData.set(ANI_MOVE, move.ordinal());}
  public int getSpecialMove() {return (this.entityData.get(SPECIAL_MOVE));}
  public void setSpecialMove(int move) {  // quick overwrite
    setAniMove(Utils.AniMove.SPECIAL);
    this.entityData.set(SPECIAL_MOVE, move);
  }

  public abstract AgentConfig getAgentConfig();

  /// Combat
  public boolean shootGun(boolean precision){   ///  true = long reload, false = just compute cooldown,
    // check for friendly on line. else dont shoot and just move to cooldown
    if (Utils.hasFriendlyInLineOfFire(this, getTarget())) {return false;}

    prepAttack();
    if (op.getSynIsBolting()) {op.aim(false);}
    if (op.getSynIsAiming() != precision) {
      op.aim(precision);
    }

    switch (op.shoot(() -> getViewXRot(1f), () -> getViewYRot(1f))) {
      case SUCCESS -> {}
      case NOT_DRAW -> {op.draw(this::getMainHandItem);}
      case NEED_BOLT -> {op.bolt();}
      case NO_AMMO -> {
        reloadGun();
        // ANI
        PacketHandler.CHANNELS.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),new EntityRenderPacket(this.getId(), 1));
        setAniMove(Utils.AniMove.RELOAD);
        level().playSound(null, this, SoundEvents.SLIME_SQUISH, SoundSource.BLOCKS, 1.2f, 0.5f);
        return true;
      }
    }
    return false;
  }

  protected void prepAttack(){  // rotate the body identical to head to avoid award calculation
    float snapYaw = getYHeadRot();
    setYRot(snapYaw);
    setYBodyRot(snapYaw);
  }
  public void reloadGun(){
    int reloadAmount = 0;
    ItemStack gunStack = getMainHandItem();
    AbstractGunItem gunItem = (AbstractGunItem)gunStack.getItem();
    ResourceLocation gunResource = gunItem.getGunId(gunStack);
    CommonGunIndex gunIndex = TimelessAPI.getCommonGunIndex(gunResource).orElse(null);
    int maxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(gunStack, gunIndex.getGunData());
    int curAmmoCount = inventory.checkGunAmmo(gunStack, gunItem);

    // if use virtual ammo
    int virtAmmo = getVirtualAmmo();
    if (virtAmmo > 0){
      reloadAmount = Math.min(virtAmmo, maxAmmoCount - curAmmoCount);
      setVirtualAmmo(virtAmmo - reloadAmount);
    } else {
      // find ammo
      int i = inventory.findGunAmmo(gunStack);
      if (i == -1) return;
      // compute amount
      ItemStack ammoStack = inventory.getItem(i);
      if (ammoStack.getItem() instanceof IAmmoBox iAmmoBoxItem) {
        reloadAmount = Math.min(maxAmmoCount - curAmmoCount, iAmmoBoxItem.getAmmoCount(ammoStack));
        iAmmoBoxItem.setAmmoCount(ammoStack,iAmmoBoxItem.getAmmoCount(ammoStack)-reloadAmount);
      } else if(ammoStack.getItem() instanceof IAmmo){
        reloadAmount = Math.min(maxAmmoCount - curAmmoCount, ammoStack.getCount());
        ammoStack.setCount(ammoStack.getCount() - reloadAmount);
      }
    }
    gunItem.setCurrentAmmoCount(gunStack,curAmmoCount+reloadAmount);
  }

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
    this.goalSelector.addGoal(6, new SaluteGoal(this, 100));
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
          Utils.FollowMode mode = nextFollowMode();
          setFollowMode(mode, player.getUUID());
          String disp = switch (mode) {
            case WANDER -> this.getName().getString() + " will wander around here";
            case STAY -> this.getName().getString() + " will stand guard";
            case FOLLOW -> {
              Entity target = ((ServerLevel) level()).getEntity(this.followPlayer);
              String name = target != null ? target.getName().getString() : "???";
              yield this.getName().getString() + " will follow " + name;
            }
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
  public boolean sameTeam(LivingEntity entity) {
    if (entity instanceof Player player) {
      return isFriendlyPlayer(player, level());
    } else if (entity instanceof IEntityTeam teamer){
      return isFriendlyMod(teamer, level());
    }
    return false;
  }

  public SimpleContainer getInventory(){return inventory;}
  public boolean wantsToPickUp(ItemStack pStack) {return true;}
  protected void pickUpItem(ItemEntity itemEntity) {
    ItemStack input = itemEntity.getItem();
    if (input.isEmpty()) return;
    // --- Armor ---
    if (input.getItem() instanceof ArmorItem armor) {
      EquipmentSlot slot = armor.getEquipmentSlot();
      int invSlot = switch (slot) {
        case HEAD -> gearSlots[0];
        case CHEST -> gearSlots[1];
        case LEGS -> gearSlots[2];
        case FEET -> gearSlots[3];
        default -> -1;
      };
      if (invSlot != -1 && inventory.getItem(invSlot).isEmpty()) {
        inventory.setItem(invSlot, input);
        setItemSlot(slot, input); // visually equip
        itemEntity.discard();
        return;
      }
    }
    // --- Gun ---
    if (isEquipableGun(input) && inventory.getItem(gunSlot).isEmpty()) {
      inventory.setItem(gunSlot, input);
      itemEntity.discard();
      return;
    }
    // --- Melee ---
    if (isEquipableMelee(input) && inventory.getItem(meleeSlot).isEmpty()) {
      inventory.setItem(meleeSlot, input);
      itemEntity.discard();
      return;
    }
    // --- Special Slot ---
    ItemStack special = inventory.getItem(specialSlot);
    if (special.isEmpty()) {
      inventory.setItem(specialSlot, input);
      itemEntity.discard();
      return;
    }
    else if (ItemStack.isSameItemSameTags(special, input) && special.getCount() < special.getMaxStackSize()) {
      int move = Math.min(input.getCount(), special.getMaxStackSize() - special.getCount());
      special.grow(move);
      input.shrink(move);
      if (input.isEmpty()) {
        itemEntity.discard();
        return;
      }
    }
    // --- Backpack (slot 7–16) ---
    for (int i = 7; i < inventory.getContainerSize(); i++) {
      ItemStack slot = inventory.getItem(i);
      if (!slot.isEmpty() && ItemStack.isSameItemSameTags(slot, input) && slot.getCount() < slot.getMaxStackSize()) {
        int move = Math.min(input.getCount(), slot.getMaxStackSize() - slot.getCount());
        slot.grow(move);
        input.shrink(move);
        if (input.isEmpty()) {
          itemEntity.discard();
          return;
        }
      }
    }
    for (int i = 7; i < inventory.getContainerSize(); i++) {
      if (inventory.getItem(i).isEmpty()) {
        inventory.setItem(i, input);
        itemEntity.discard();
        return;
      }
    }
    // ❌ Couldn’t insert fully
    itemEntity.setItem(input);
  }

  public boolean hurt(DamageSource source, float amount) {
    Entity entity = source.getEntity();
    if (invincible) {
      level().playSound(null, this, SoundEvents.SHIELD_BLOCK, SoundSource.BLOCKS, 1.0f, 0.5f);
      return false;
    }
    if (entity != null) {
      if (entity instanceof LivingEntity living && !sameTeam(living)) {this.setTarget(living);}
    }
    return super.hurt(source, amount);
  }
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
    nbt.putString("skin",getSkin());
//    nbt.putBoolean("attack_player", this.getAttackPlayer());
    nbt.putInt("movement", this.entityData.get(MOVEMENT_F));
    nbt.putInt("virtual_ammo", this.getVirtualAmmo());
  }
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
    setSkin(nbt.getString("skin"));
//    this.setAttackPlayer(nbt.getBoolean("attack_player"));
    this.setFollowMode(Utils.FollowMode.values()[nbt.getInt("movement")], this.getBossUUID());
    this.setVirtualAmmo(nbt.getInt("virtual_ammo"));
    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
  }

  public void initCosmetic(){
    boolean isFemale = ThreadLocalRandom.current().nextBoolean();
    this.setCustomName(Component.literal(Utils.randomName(isFemale)));
    setFemale(isFemale);
    setSkin(isFemale ? Utils.makeSafeSkinName(getAgentConfig().defaultFemaleSkin) : Utils.makeSafeSkinName(getAgentConfig().defaultMaleSkin));
  }
  public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
    SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    this.setLeftHanded(false);  // everyone use right hand pls
    if (!persistedFromNBT) {
      boolean isFemale = ThreadLocalRandom.current().nextBoolean();
      this.setCustomName(Component.literal(Utils.randomName(isFemale)));
      setFemale(isFemale);
      setSkin(isFemale ? Utils.makeSafeSkinName(getAgentConfig().defaultFemaleSkin) : Utils.makeSafeSkinName(getAgentConfig().defaultMaleSkin));
    }
    return data;
  }
  protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
    super.dropCustomDeathLoot(source, looting, recentlyHit);
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        this.spawnAtLocation(stack);
      }
    }
  }

  private void passiveRegen() {
    this.setFood(this.getFood() - 1);
    this.heal(1);
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
  public AbstractContainerMenu createMenu(int containerID, Inventory inventory, Player player) {
    return new AgentInventoryMenu(containerID, inventory, this);
  }

  //just stack to mainhand, perfrom check since it call draw, which take entity a few tick to process drawing
  public void equipGun() {
    if (getMainHandItem() != inventory.getItem(gunSlot)){
      setItemInHand(InteractionHand.MAIN_HAND, inventory.getItem(gunSlot));
      IGunOperator op = IGunOperator.fromLivingEntity(this);
      op.draw(this::getMainHandItem);
    }
  }
  // just stack to mainhand
  public void equipMelee() {setItemInHand(InteractionHand.MAIN_HAND, inventory.getItem(meleeSlot));}
  // special
  public void equipSpecial(boolean offhand) {setItemInHand(offhand? InteractionHand.OFF_HAND: InteractionHand.MAIN_HAND, inventory.getItem(specialSlot));}
  public ItemStack getSpecialSlot() {return inventory.getItem(specialSlot);}
  public ItemStack getGunSlot(){return inventory.getItem(gunSlot);}

  // for overwrite later in final class
  public boolean isEquipableGun(ItemStack stack) {
    CompoundTag nbt = stack.getOrCreateTag();
    String gunId = nbt.getString(GUN_ID_TAG);
    if (gunId.isEmpty()) return false;
    return getAgentConfig().allowGuns.contains(gunId);
  }
  public boolean isEquipableMelee(ItemStack stack) {
    if (stack.isEmpty()) return false;
    return (getAgentConfig().allowMelees.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()) ||
        stack.getTags().anyMatch(tagKey -> getAgentConfig().allowMelees.contains("#" + tagKey.location())));
  }
  public int getMaxVirtualAmmo(){return getAgentConfig().maxVirtualAmmo;}
  public int getAmmoPerCharge(){return getAgentConfig().chargePerAmmo;}


}
