package com.github.ptran779.aegisops.config;

import java.util.Set;

public class AgentConfig {
  public int maxVirtualAmmo;  // how many charge virtual ammo can a class carry
  public int chargePerAmmo;   // how many charge do you need to recharge per virtual ammo
  public String defaultMaleSkin;
  public String defaultFemaleSkin;
  public Set<String> allowGuns = null;
  public Set<String> allowMelees = null;
  public boolean isValid() {
    return maxVirtualAmmo >= 0 && chargePerAmmo > 0;
  }
}
