package com.github.ptran779.aegisops.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public class LayerConfigWidget extends AbstractWidget {
  public static final List<String> LAYER_TYPE = List.of("Dense", "Relu", "Leaky Relu", "RNN");

  // Components
  public final DropDownWidget typeSelector;
  public final EditBox outputSizeBox;

  // Layout Constants
  private static final int WIDGET_HEIGHT = 16;

  public LayerConfigWidget(int x, int y, int width, int height, String initialType, int initialSize) { // alpha arg ignored now
    super(x, y, width, height, Component.empty());

    Font font = Minecraft.getInstance().font;
    int padding = 4;

    // 1. Layer Type Selector (Top)
    this.typeSelector = new DropDownWidget(x + padding, y + 4, width - (padding * 2), 16, 4,
        initialType, LAYER_TYPE, (s) -> {}); // No callback needed

    // 2. Output Size Box (Bottom)
    this.outputSizeBox = new EditBox(font, x + padding, y + 40, width - (padding * 2), WIDGET_HEIGHT, Component.literal("Nodes"));
    this.outputSizeBox.setValue(String.valueOf(initialSize));
    this.outputSizeBox.setFilter(s -> s.matches("\\d*")); // Numbers only
    this.outputSizeBox.setBordered(true);
  }

  public void unlock(boolean unlock) {
    typeSelector.active = unlock;
    outputSizeBox.setEditable(unlock);
  }


  public void setX(int x) {
    super.setX(x);
    updateChildrenPos();
  }

  @Override
  public void setY(int y) {
    super.setY(y);
    updateChildrenPos();
  }

  private void updateChildrenPos() {
    int padding = 4;
    if (this.typeSelector != null) {
      this.typeSelector.setX(this.getX() + padding);
      this.typeSelector.setY(this.getY() + 4);
    }
    if (this.outputSizeBox != null) {
      this.outputSizeBox.setX(this.getX() + padding);
      this.outputSizeBox.setY(this.getY() + 40);
    }
  }

  // --- Rendering ---

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    // Background
    guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF00FFFF); // Cyan Border
    guiGraphics.fill(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, 0xFF222222); // Dark Grey Fill

    Font font = Minecraft.getInstance().font;

    //if activation layer, skip the size option
    if (!Objects.equals(typeSelector.getSelected(), "Relu") &&
        !Objects.equals(typeSelector.getSelected(), "Leaky Relu")) {
        guiGraphics.drawString(font, "Size:", getX() + 5, getY() + 30, 0xAAAAAA, false);

       this.outputSizeBox.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // Render Dropdown LAST (High Z-Index)
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(0, 0, 100);
    this.typeSelector.render(guiGraphics, mouseX, mouseY, partialTick);
    guiGraphics.pose().popPose();
  }

  // --- Input Forwarding ---

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    // A. Dropdown
    if (this.typeSelector.mouseClicked(mouseX, mouseY, button)) {
      this.outputSizeBox.setFocused(false);
      return true;
    }

    // Only allow clicking if the box is both VISIBLE and ACTIVE (Unlocked)
    if (this.outputSizeBox.visible && this.outputSizeBox.active) {
      if (this.outputSizeBox.mouseClicked(mouseX, mouseY, button)) {
        this.outputSizeBox.setFocused(true);
        return true;
      }
    }

    // C. Clicked background/nothing
    this.outputSizeBox.setFocused(false);
    return false;
  }

  // 3. THE FIX: Sync Focus State
// If the Screen tells the Layer "You are focused", pass that to the EditBox
  @Override
  public void setFocused(boolean focused) {
    super.setFocused(focused);
    if (focused) {
      // Automatically focus the text box when the user tabs to this widget
      this.outputSizeBox.setFocused(true);
    } else {
      this.outputSizeBox.setFocused(false);
    }
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    if (this.outputSizeBox.charTyped(codePoint, modifiers)) return true;
    return super.charTyped(codePoint, modifiers);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (this.outputSizeBox.keyPressed(keyCode, scanCode, modifiers)) return true;
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}