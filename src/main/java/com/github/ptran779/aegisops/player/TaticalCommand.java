package com.github.ptran779.aegisops.player;

import com.github.ptran779.aegisops.Utils;
import net.minecraft.nbt.CompoundTag;

public class TaticalCommand {
  private Utils.FollowMode follow_mode = Utils.FollowMode.WANDER;
  private Utils.TargetMode target_mode = Utils.TargetMode.ENEMY_AGENTS;
  private boolean special_mode = false;

  // --- Follow ---
  public Utils.FollowMode getFollowMode() { return follow_mode; }
  public void cycleFollowMode() { follow_mode = Utils.FollowMode.nextFollowMode(follow_mode.ordinal()); }

  // --- Target ---
  public Utils.TargetMode getTargetMode() { return target_mode; }
  public void cycleTargetMode() { target_mode = Utils.TargetMode.nextTargetMode(target_mode.ordinal()); }

  // --- Special ---
  public boolean isSpecialMode() { return special_mode; }
  public void toggleSpecialMode() { special_mode = !special_mode; }


  public void copyFrom(TaticalCommand source) {
    follow_mode = source.follow_mode;
    target_mode = source.target_mode;
    special_mode = source.special_mode;
  }

  public void saveNBTData(CompoundTag nbt) {
    nbt.putInt("follow_mode", follow_mode.ordinal());
    nbt.putInt("target_mode", target_mode.ordinal());
    nbt.putBoolean("special_mode", special_mode);
  }

  public void loadNBTData(CompoundTag nbt) {
    follow_mode = Utils.FollowMode.values()[nbt.getInt("follow_mode")];
    target_mode = Utils.TargetMode.values()[nbt.getInt("target_mode")];
    special_mode = nbt.getBoolean("special_mode");
  }
}
