package com.github.ptran779.aegisops.Config;

import java.util.Set;

public class AgentConfig {
  public Set<String> allowGuns = null;
  public Set<String> allowMelees = null;
  public Set<String> allowSpecials = null;  /// wip not used for now
  public int maxVirtualAmmo;  // how many charge virtual ammo can a class carry
  public int chargePerAmmo;   // how many charge do you need to recharge per virtual ammo

  public boolean isValid() {
    return maxVirtualAmmo >= 0 && chargePerAmmo > 0;
  }
}
