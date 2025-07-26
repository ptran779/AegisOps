package com.github.ptran779.aegisops.effect;

import com.github.ptran779.aegisops.server.AttributeInit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class BattleFlow extends MobEffect {
  public BattleFlow(MobEffectCategory pCategory, int pColor) {
    super(pCategory, pColor);
    // Apply 10% faster fire rate (attack speed)
    this.addAttributeModifier(
        AttributeInit.AGENT_ATTACK_SPEED_ATTR.get(),
        "0dd0ac81-b23c-485a-8351-345910482696",
        0.0D,  // ignore for now
        AttributeModifier.Operation.MULTIPLY_BASE
    );
  }
  public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
    return 0.1 + 0.05*amplifier; // ← auto scale
  }
}
