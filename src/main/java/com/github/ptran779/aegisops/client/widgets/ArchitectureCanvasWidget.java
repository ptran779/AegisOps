package com.github.ptran779.aegisops.client.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ArchitectureCanvasWidget extends AbstractWidget {

  private final List<LayerConfigWidget> layers = new ArrayList<>();
  private double scrollAmount = 0;

  // Horizontal Layout Config
  private final int cardWidth; // Fixed width for each column
  private final int cardHeight; // Fixed width for each column
  private final int spacing;      // Space between columns

  public ArchitectureCanvasWidget(int x, int y, int width, int height, int cardWidth, int cardHeight, int spacing) {
    super(x, y, width, height, Component.empty());
    this.cardWidth = cardWidth;
    this.cardHeight = cardHeight;
    this.spacing = spacing;
  }

  public void addLayer(LayerConfigWidget widget) {
    this.layers.add(widget);
    // Force the widget to the standard column width
    widget.setWidth(cardWidth);
    widget.setHeight(cardHeight);

    repositionChildren();
  }

  public void popLayer(){
    if (!layers.isEmpty()) {layers.remove(layers.size() - 1);}
  }

  public List<LayerConfigWidget> getLayers() { return layers; }

  public void clearLayers() {layers.clear();}

  public void unlock(boolean unlock) {
    for (LayerConfigWidget widget : layers) {widget.unlock(unlock);}
  }

  // Updates X positions based on horizontal scroll.
  private void repositionChildren() {
    // Start X position (scrolled)
    int currentX = this.getX() + spacing - (int) scrollAmount;
    // Center Y position (or top aligned)
    int fixedY = this.getY() + 5;

    for (LayerConfigWidget layer : layers) {
      layer.setX(currentX);
      layer.setY(fixedY);

      // Move to next column
      currentX += cardWidth + spacing;
    }
  }

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    // 1. Background & Border
    guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000);

    // 2. Scissor (Clipping)
    guiGraphics.enableScissor(getX(), getY(), getX() + width, getY() + height);

    // 3. Render Children
    // (Standard loop is fine for Horizontal, as dropdowns usually fall DOWN into empty space)
    for (LayerConfigWidget layer : layers) {
      // Optimization: Only render if visible horizontally
      if (layer.getX() + layer.getWidth() > this.getX() && layer.getX() < this.getX() + this.width) {
        layer.render(guiGraphics, mouseX, mouseY, partialTick);

        // Optional: Draw an arrow "->" between layers
        if (layers.indexOf(layer) < layers.size() - 1) {
          guiGraphics.drawString(
              net.minecraft.client.Minecraft.getInstance().font,
              ">>",
              layer.getX() + layer.getWidth() + 1,
              layer.getY() + 40,
              0xFF00FFFF,
              false
          );
        }
      }
    }

    // 4. Disable Scissor
    guiGraphics.disableScissor();

    // 5. Horizontal Scrollbar Indicator (Bottom)
    int contentWidth = getMaxScroll() + this.width; // Approximation for bar math
    int maxScroll = getMaxScroll();
    if (maxScroll > 0) {
      int barWidth = (int) ((float) width / contentWidth * width);
      // Clamp bar width
      barWidth = Math.max(30, Math.min(width, barWidth));

      int barX = getX() + (int) ((scrollAmount / maxScroll) * (width - barWidth));
      int barY = getY() + height - 6;

      guiGraphics.fill(barX, barY, barX + barWidth, barY + 4, 0xFF888888);
    }
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (!isMouseOver(mouseX, mouseY)) return false;

    // Scroll X instead of Y
    // "delta" is usually vertical scroll wheel (+1/-1).
    // We treat vertical wheel as horizontal movement here (standard for wide lists).
    this.scrollAmount = Mth.clamp(this.scrollAmount - (delta * 20), 0, getMaxScroll());
    repositionChildren();

    return true;
  }

  private int getMaxScroll() {
    int totalContentWidth = layers.size() * (cardWidth + spacing);
    // How much can we scroll? (Total width - Viewport width)
    return Math.max(0, totalContentWidth - this.width + spacing);
  }

  // Inside ArchitectureCanvasWidget.java

  private LayerConfigWidget focusedLayer = null;

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (!isMouseOver(mouseX, mouseY)) return false;

    for (int i = layers.size() - 1; i >= 0; i--) {
      LayerConfigWidget layer = layers.get(i);
      if (layer.getX() + layer.getWidth() > this.getX() && layer.getX() < this.getX() + this.width) {
        if (layer.mouseClicked(mouseX, mouseY, button)) {

          // Update internal focus tracking
          if (focusedLayer != null && focusedLayer != layer) {
            focusedLayer.setFocused(false); // Unfocus old layer
          }
          focusedLayer = layer;
          focusedLayer.setFocused(true); // Focus new layer

          return true;
        }
      }
    }

    // If we clicked empty space, clear focus
    if (focusedLayer != null) {
      focusedLayer.setFocused(false);
      focusedLayer = null;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    // Only send to the focused layer!
    if (focusedLayer != null) {
      return focusedLayer.charTyped(codePoint, modifiers);
    }
    return super.charTyped(codePoint, modifiers);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    // Only send to the focused layer!
    if (focusedLayer != null) {
      return focusedLayer.keyPressed(keyCode, scanCode, modifiers);
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}