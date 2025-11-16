package com.github.ptran779.aegisops.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import static com.github.ptran779.aegisops.AegisOps.MOD_ID;

public class KeyBinding {
  public static final String KEY_CATEGORY = String.format("key.category.%s.%s", MOD_ID, MOD_ID);
  public static final String FOLLOW_MODE = String.format("key.category.%s.follow_mode", MOD_ID);
  public static final String TARGET_MODE = String.format("key.category.%s.target_mode", MOD_ID);
  public static final String SPECIAL_MODE = String.format("key.category.%s.special_mode", MOD_ID);

  public static final KeyMapping FOLLOW_KEY = new KeyMapping(FOLLOW_MODE, KeyConflictContext.IN_GAME,
      KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F, KEY_CATEGORY);
  public static final KeyMapping TARGET_KEY = new KeyMapping(TARGET_MODE, KeyConflictContext.IN_GAME,
      KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_T, KEY_CATEGORY);
  public static final KeyMapping SPECIAL_KEY = new KeyMapping(SPECIAL_MODE, KeyConflictContext.IN_GAME,
      KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_S, KEY_CATEGORY);
}
