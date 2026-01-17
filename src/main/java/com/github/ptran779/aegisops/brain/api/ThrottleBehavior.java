package com.github.ptran779.aegisops.brain.api;

import net.minecraft.world.entity.Entity;

// it throttle can use check, not the actual logic, else wtf do we have this :)
public abstract class ThrottleBehavior extends Behavior {
  protected final int BASE_COOLDOWN;
  protected final int VAR_COOLDOWN;
  protected int cooldown;
  protected int lastcooldown;
  protected final Entity entity;

  public ThrottleBehavior(int baseCooldown, int varCooldown, Entity entity) {
    BASE_COOLDOWN = baseCooldown;
    VAR_COOLDOWN = varCooldown;
    cooldown = baseCooldown;
    lastcooldown = 0;
    this.entity = entity;
  }

  @Override
  public boolean canUse() {
    return entity.tickCount - lastcooldown > cooldown;
  }

  public void stop(){
    lastcooldown = entity.tickCount;
    cooldown = generateCooldown();
  }

  public void interrupt(){
    lastcooldown = entity.tickCount;
    cooldown = generateCooldown();
  }

  protected int generateCooldown() {
    if (VAR_COOLDOWN > 0) {return BASE_COOLDOWN - VAR_COOLDOWN/2 + entity.level().random.nextInt(VAR_COOLDOWN + 1);}
    else {return BASE_COOLDOWN;}
  }
}
