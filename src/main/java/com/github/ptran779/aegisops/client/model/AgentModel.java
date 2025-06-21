package com.github.ptran779.aegisops.client.model;

import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class AgentModel extends PlayerModel<AbstractAgentEntity> {
  public AgentModel(ModelPart pRoot, boolean pSlim) {
    super(pRoot, pSlim);
  }

  public void setupAnim(AbstractAgentEntity agent, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw, float headPitch) {
    super.setupAnim(agent, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

    // using item
    if (agent.isUsingItem()) {
      //eating type
      if (agent.getUseItem().isEdible()){
        int ticks = agent.getTicksUsingItem();
        // boby head
        this.head.xRot -= 0.20F * Mth.sin((float) ticks * 0.4f *Mth.PI);
        this.hat.xRot = this.head.xRot;
        //raise the arm
        this.leftArm.xRot = -Mth.HALF_PI; // = -1.5708F
        this.leftArm.yRot = 0.2F; // inward bend
        this.leftSleeve.copyFrom(this.leftArm);
      }
    } else if(agent.swinging && agent.tickCount % 2 == 0) {agent.updateSwingTime();}
  }
}
