package com.github.ptran779.aegisops.client;

import net.minecraft.client.model.geom.ModelPart;

import java.util.List;
import java.util.Map;

public interface IBoneHierachy {
//  Map<String, List<String>> getFullBoneHierachy();
  List<String> getBoneChild(String boneName);
  ModelPart get(String boneName);
  String getRootBoneName();
  ModelPart getRoot();
}
