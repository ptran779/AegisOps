package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.github.ptran779.aegisops.attribute.AgentAttribute.AGENT_ATTACK_SPEED;

public class AttributeInit {
  public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, AegisOps.MOD_ID);

  public static final RegistryObject<Attribute> AGENT_ATTACK_SPEED_INIT =ATTRIBUTES.register("agent_attack_speed", ()-> AGENT_ATTACK_SPEED);
}