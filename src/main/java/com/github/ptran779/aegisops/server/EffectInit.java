package com.github.ptran779.aegisops.server;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.effect.BattleFlow;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EffectInit {
  public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, AegisOps.MOD_ID);
  public static final RegistryObject<MobEffect> BATTLE_FLOW = EFFECTS.register("battle_flow",() -> new BattleFlow(MobEffectCategory.BENEFICIAL, 0xFF5500));
}
