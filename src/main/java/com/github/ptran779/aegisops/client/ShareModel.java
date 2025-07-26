package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.client.model.GrenadeModel;
import com.github.ptran779.aegisops.client.model.HellpodModel;
import net.minecraft.client.model.geom.ModelPart;

public class ShareModel {
  /// Use to share complex model if possible
  public static HellpodModel HELLPOD_MODEL_SHARE;
  public static GrenadeModel GRENADE_MODEL_SHARE;

  public static HellpodModel prepHellPodModel(ModelPart part) {
    if (HELLPOD_MODEL_SHARE == null) {HELLPOD_MODEL_SHARE = new HellpodModel(part);}
    return HELLPOD_MODEL_SHARE;
  }

  public static GrenadeModel prepGrenadeModel(ModelPart part) {
    if (GRENADE_MODEL_SHARE == null) {GRENADE_MODEL_SHARE = new GrenadeModel(part);}
    return GRENADE_MODEL_SHARE;
  }
}
