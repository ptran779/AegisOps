package com.github.ptran779.aegisops.client.model;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.HashMap;
import java.util.Map;

public class AgentModel extends PlayerModel<AbstractAgentEntity>{
  public final Map<String, ModelPart> BONE_PARTS = new HashMap<>();

  public AgentModel(ModelPart pRoot, boolean pSlim) {
    super(pRoot, pSlim);
    // code in the map for lookup:
    BONE_PARTS.put("Head", this.head);
    BONE_PARTS.put("Body", this.body);
    BONE_PARTS.put("LeftArm", this.leftArm);
    BONE_PARTS.put("RightArm", this.rightArm);
    BONE_PARTS.put("LeftLeg", this.leftLeg);
    BONE_PARTS.put("RightLeg", this.rightLeg);
  }

  public void setupAnim(AbstractAgentEntity agent, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
