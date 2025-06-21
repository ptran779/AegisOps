package com.github.ptran779.aegisops.entity.util;

import com.github.ptran779.aegisops.Config.ShareResource;
import com.github.ptran779.aegisops.attribute.AgentAttribute;
import com.github.ptran779.aegisops.goal.AgentAttackGoal;
import com.github.ptran779.aegisops.goal.CustomRangeTargetGoal;
import com.github.ptran779.aegisops.goal.EatFoodGoal;
import com.github.ptran779.aegisops.goal.FollowGoal;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerScoreboard;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.ptran779.aegisops.attribute.AgentAttribute.*;

public abstract class AbstractAgentEntity extends PathfinderMob implements MenuProvider, IAgentEntity {
    public String agentType = "Template";
    public AgentInventory inventory = new AgentInventory(16, this);  // so I can handle inventory related stuff cleaner
    public final int[] gearSlots = {inventory.getContainerSize()-4,inventory.getContainerSize()-3,inventory.getContainerSize()-2,inventory.getContainerSize()-1};
    public final int gunSlot = inventory.getContainerSize()-7;
    public final int meleeSlot = inventory.getContainerSize()-6;
    public final int specialSlot = inventory.getContainerSize()-5;

    private String owner = "";
    private String team = "";
    public String followPlayer = "";

    public int maxfood = 40;  // FIXME subtract for action?

    // auto sync variable. Useful for setting flag
    private static final EntityDataAccessor<Boolean> AUTO_ARMOR_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FOLLOW_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_HOSTILE_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PLAYER_HOSTILE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Boolean> KEEP_EAT_F = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> FOOD_VALUE = SynchedEntityData.defineId(AbstractAgentEntity.class, EntityDataSerializers.INT);

    public AbstractAgentEntity(EntityType<? extends AbstractAgentEntity> entityType, Level level) {
        super(entityType, level);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20)
            .add(Attributes.MOVEMENT_SPEED, 0.5)
            .add(Attributes.JUMP_STRENGTH, 1)
            .add(Attributes.FOLLOW_RANGE, 16)
            .add(Attributes.ATTACK_DAMAGE, 1)
            .add(AgentAttribute.AGENT_ATTACK_SPEED, 1);
    }

    // extra sync data for client and server
    protected void defineSynchedData(){
        super.defineSynchedData();
        entityData.define(AUTO_ARMOR_F, false);
        entityData.define(FOLLOW_F, false);
        entityData.define(AUTO_HOSTILE_F, false);
        entityData.define(KEEP_EAT_F, false);
        entityData.define(FOOD_VALUE, 0);
    }

    public boolean getAutoArmor() {return this.entityData.get(AUTO_ARMOR_F);}
    public void setAutoArmor(boolean flag) {this.entityData.set(AUTO_ARMOR_F, flag);}
    public boolean getKeepEating() {return this.entityData.get(KEEP_EAT_F);}
    public void setKeepEating(boolean flag) {this.entityData.set(KEEP_EAT_F, flag);}
    public boolean getFollow() {return this.entityData.get(FOLLOW_F);}
    public void setFollow(boolean flag, String player) {
        this.entityData.set(FOLLOW_F, flag);
        this.followPlayer = getFollow() ? player : "";
    }
    public boolean getAutoHostile() {return this.entityData.get(AUTO_HOSTILE_F);}
    public void setAutoHostile(boolean flag) {this.entityData.set(AUTO_HOSTILE_F, flag);}

    public Integer getFood() {return this.entityData.get(FOOD_VALUE);}
    public void setFood(Integer val) {this.entityData.set(FOOD_VALUE, val);}


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new OpenDoorGoal(this, true));

        this.goalSelector.addGoal(3, new CustomRangeTargetGoal(this, LivingEntity.class, 40, 48, true,
            entity -> entity instanceof Enemy
        ));
        this.goalSelector.addGoal(3, new AgentAttackGoal(this, 6, 12, 64, 64));
        this.goalSelector.addGoal(5, new FollowGoal(this));
        this.goalSelector.addGoal(4, new EatFoodGoal(this));
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(!this.level().isClientSide()) {
            // send packet
            NetworkHooks.openScreen((ServerPlayer) player, this, buf -> buf.writeInt(this.getId()));
            if (owner.isEmpty()) {owner = player.getGameProfile().getName();}
        }
        return InteractionResult.SUCCESS;
    }

    public boolean sameTeam(LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getName().getString().equals(owner)) {return true;}  // its the owner
            Team otherTeam = player.getTeam();
            if (otherTeam == null) {return false;}  // no team
            return otherTeam.getName().equals(this.team);  // same team
        } else if (entity instanceof AbstractAgentEntity otherAgent){
            return (otherAgent.owner.equals(owner)) || (team.equals(otherAgent.team));
        }
        return false;
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
        nbt.putString("owner", owner);
        nbt.putString("team", team);
        nbt.putBoolean("auto_armor", this.getAutoArmor());
        nbt.putBoolean("auto_hostile", this.getAutoHostile());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        // Load inventory
        ListTag invTag = nbt.getList("AgentInventory", Tag.TAG_COMPOUND);
        for (int i = 0; i < invTag.size(); i++) {
            CompoundTag itemTag = invTag.getCompound(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < this.inventory.getContainerSize()) {this.inventory.setItem(slot, ItemStack.of(itemTag));}
        }

        // load other data
        setFood(nbt.getInt("Food"));
        this.owner = nbt.getString("owner");
        this.team = nbt.getString("team");
        this.setAutoArmor(nbt.getBoolean("auto_armor"));
        this.setAutoHostile(nbt.getBoolean("auto_hostile"));
    }

    public ResourceLocation getTexture() {return ShareResource.skins[0];}

    public void showTeam(){
        System.out.println("print debug information here");
        // this.level is the world the entity lives in
        if (!(this.getCommandSenderWorld() instanceof ServerLevel serverLevel)) {
            System.out.println("showTeam() only works on the logical server!");
            return;
        }

        ServerScoreboard scoreboard = serverLevel.getScoreboard();
        Collection<PlayerTeam> teams = scoreboard.getPlayerTeams();  // get all teams :contentReference[oaicite:2]{index=2}

        if (teams.isEmpty()) {
            System.out.println("No teams found.");
            return;
        }

        for (PlayerTeam team : teams) {
            System.out.println("Team: " + team.getName());

            Collection<String> members = team.getPlayers();
            if (members.isEmpty()) {
                System.out.println("  (no members)");
            } else {
                for (String entry : members) {
                    System.out.println("  - " + entry);
                }
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
                    attr.removeModifier(AgentAttribute.WELL_FEED_SPEED_BOOST);
                }
            }
            if (getHealth()<getMaxHealth() && getFood() >= maxfood * 0.25) {
                passiveRegen();
            }
//            level().getScoreboard().getPlayersTeam();
        }
    }

    private void passiveRegen() {
        this.setFood(this.getFood() - 1);
        this.heal(1);
    }

    /// client stuff
    //menu
    @Override
    public AbstractContainerMenu createMenu(int containerID, Inventory inventory, Player player) {
        return new AgentInventoryMenu(containerID, inventory, this);
    }

    public void updateSwingTime(){super.updateSwingTime();}

    public String getOwner() {
        return owner;
    }

//    public void setOwner(Player owner) {
//    this.owner = owner;
//    }

    public void clearTarget() {setTarget(null);}

    //just stack to mainhand, perfrom check since it call draw, which take entity a few tick to process drawing
    public void equipGun() {
        if (getMainHandItem() != inventory.getItem(gunSlot)){
            setItemSlot(EquipmentSlot.MAINHAND, inventory.getItem(gunSlot));
            IGunOperator op = IGunOperator.fromLivingEntity(this);
            op.draw(this::getMainHandItem);
        }

    }

    //just stack to mainhand
    public void equipMelee() {setItemSlot(EquipmentSlot.MAINHAND, inventory.getItem(meleeSlot));}
}
