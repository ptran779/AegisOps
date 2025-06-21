package com.github.ptran779.aegisops.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;

public class ToggleButton extends ImageButton {
  private BooleanSupplier onState;

  public ToggleButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, BooleanSupplier supplier, OnPress pOnPress) {
    super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress);
    this.onState = supplier;
  }
  @Override
  public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
    this.renderTexture(pGuiGraphics, this.resourceLocation, this.getX(), this.getY(), this.xTexStart, onState.getAsBoolean() ? this.yDiffTex : 0, 0, this.width, this.height, this.textureWidth, this.textureHeight);
  }
}
