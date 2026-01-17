package com.github.ptran779.aegisops.attribute;
import com.github.ptran779.aegisops.AegisOps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;


import java.util.UUID;

public class AgentAttribute {
  /// original
  public static Attribute AGENT_ATTACK_SPEED= new RangedAttribute("attribute.name." +AegisOps.MOD_ID + ".agent_attack_speed", 2.0F, 0.000001F, 400.0F).setSyncable(true);

  /// Modifier
//  public static final UUID WELL_FEED_SPEED_BOOST_UUID = UUID.fromString("5f27715e-f97d-4266-a4a5-f76cf488414b");
  public static final AttributeModifier WELL_FEED_SPEED_BOOST = new AttributeModifier(UUID.fromString("5f27715e-f97d-4266-a4a5-f76cf488414b"), "Well-fed speed boost", 0.20, AttributeModifier.Operation.MULTIPLY_BASE);
}
