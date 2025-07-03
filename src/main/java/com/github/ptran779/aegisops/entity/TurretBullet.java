package com.github.ptran779.aegisops.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

public class TurretBullet extends Projectile {
  float dmg;
  int tickKill;
  public TurretBullet(EntityType<? extends TurretBullet> type, Level level) {
    super(type, level);
    this.noPhysics = false;
    dmg = 4f;
    tickKill = 60;
  }

  public void init(double dmg) {
    this.dmg = (float) dmg;
  }

  @Override
  protected void defineSynchedData() {}

  public void tick() {
    Entity entity = this.getOwner();
    if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
      super.tick();

      HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      if (hitresult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
        this.onHit(hitresult);
      }

      this.checkInsideBlocks();
      this.setPos(
          this.getX() + this.getDeltaMovement().x,
          this.getY() + this.getDeltaMovement().y,
          this.getZ() + this.getDeltaMovement().z
      );
      ProjectileUtil.rotateTowardsMovement(this, 0.2F);

    } else {
      this.discard();
    }
  }

  @Override
  public boolean isNoGravity() {
    return true;
  }

  @Override
  protected void onHitEntity(EntityHitResult result) {
    Entity target = result.getEntity();
    Entity owner = this.getOwner();

    if (target.hurt(damageSources().thrown(this, owner), dmg)) {}

    this.discard();
  }

  @Override
  protected void onHitBlock(BlockHitResult result) {
    super.onHitBlock(result);
    this.discard(); // hit block = despawn
  }
}