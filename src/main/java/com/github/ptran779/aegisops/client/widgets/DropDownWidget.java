package com.github.ptran779.aegisops.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.Consumer;

public class DropDownWidget extends AbstractWidget {
  private List<String> options;
  private final Consumer<String> onSelect;

  private boolean open = false;
  private int scroll = 0;
  private String currentOption;

  // Width Handling
  private int expandedWidth;

  private final int maxVisible;
  private static final int ENTRY_HEIGHT = 14;

  public DropDownWidget(int x, int y, int width, int height, int maxVisible, String currentOption, List<String> options, Consumer<String> onSelect) {
    super(x, y, width, height, Component.empty());
    this.currentOption = currentOption;
    this.options = options;
    this.onSelect = onSelect;
    this.maxVisible = maxVisible;

    // Calculate initial width
    recalculateWidth();
  }

  public void setOptions(List<String> newOptions) {
    this.options = newOptions;
    this.scroll = 0;

    // Recalculate width whenever options change
    recalculateWidth();

    if (this.currentOption != null && !this.options.contains(this.currentOption)) {
      if (!this.options.isEmpty()) {
        this.currentOption = this.options.get(0);
        this.onSelect.accept(this.currentOption);
      } else {
        this.currentOption = "";
      }
    }
  }

  private void recalculateWidth() {
    Font font = Minecraft.getInstance().font;
    int maxTextWidth = 0;
    if (options != null) {
      for (String s : options) {
        maxTextWidth = Math.max(maxTextWidth, font.width(s));
      }
    }
    // Width = Text + Padding (12px) + ScrollBar Space (4px)
    // Ensure it is at least as wide as the base button
    this.expandedWidth = Math.max(this.width, maxTextWidth + 16);
  }

  // --- HIT DETECTION & SCROLLING ---
  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    if (!visible) return false;

    // 1. Check Main Button (Standard Width)
    if (mouseX >= getX() && mouseX <= getX() + width &&
        mouseY >= getY() && mouseY <= getY() + height) {
      return true;
    }

    // 2. Check Open List (EXPANDED Width)
    if (open) {
      int listHeight = Math.min(options.size(), maxVisible) * ENTRY_HEIGHT;
      if (mouseX >= getX() && mouseX <= getX() + expandedWidth &&
          mouseY >= getY() + height && mouseY <= getY() + height + listHeight) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (!open || options.size() <= maxVisible) return false;

    if (delta != 0) {
      scroll = (int) (scroll - Math.signum(delta));
      scroll = Mth.clamp(scroll, 0, Math.max(0, options.size() - maxVisible));
      return true;
    }
    return false;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    // isMouseOver now checks expandedWidth, so this passes correctly
    if (!visible || !isMouseOver(mouseX, mouseY)) return false;

    if (button == 0) {
      // 1. Check Main Button Click
      if (mouseY <= getY() + height) {
        this.playDownSound(Minecraft.getInstance().getSoundManager());
        open = !open;
        return true;
      }

      // 2. Check List Click
      if (open) {
        int startY = getY() + height;
        int index = (int) ((mouseY - startY) / ENTRY_HEIGHT) + scroll;

        if (index >= 0 && index < options.size()) {
          currentOption = options.get(index);
          onSelect.accept(currentOption);
          this.playDownSound(Minecraft.getInstance().getSoundManager());
          open = false;
          return true;
        }
      }
    }
    return false;
  }

  // --- RENDERING ---
  @Override
  public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float delta) {
    Minecraft mc = Minecraft.getInstance();
    Font font = mc.font;

    // 1. Draw Main Button (Standard Width)
    g.fill(getX(), getY(), getX() + width, getY() + height, 0xFF444444);
    g.renderOutline(getX(), getY(), width, height, 0xFF000000);

    String label = font.plainSubstrByWidth(currentOption, width - 15);
    if (label.length() < currentOption.length()) label += "..";

    g.drawString(font, label, getX() + 4, getY() + (height - 8) / 2, 0xFFFFFF, false);
    g.drawString(font, "▼", getX() + width - 10, getY() + (height - 8) / 2, 0xAAAAAA, false);

    // 2. Draw Popup List (Expanded Width)
    if (options != null && !options.isEmpty() && open) {
      PoseStack pose = g.pose();
      pose.pushPose();

      // Z-Offset: 300 puts us comfortably above most widgets
      pose.translate(0, 0, 300);

      // IMPORTANT: Disable Depth Test to force the background to draw
      RenderSystem.disableDepthTest();

      int startY = getY() + height;
      int actualCount = Math.min(options.size(), maxVisible);
      int listHeight = actualCount * ENTRY_HEIGHT;

      // Background - Solid Black - Uses EXPANDED WIDTH
      g.fill(getX(), startY, getX() + expandedWidth, startY + listHeight, 0xFF000000);
      // Border - Cyan - Uses EXPANDED WIDTH
      g.renderOutline(getX(), startY, expandedWidth, listHeight, 0xFF00F5F7);

      for (int i = 0; i < actualCount; i++) {
        int index = scroll + i;
        if (index >= options.size()) break;

        int itemY = startY + (i * ENTRY_HEIGHT);

        // Hover Highlight - Uses EXPANDED WIDTH
        boolean isHovered = mouseX >= getX() && mouseX < getX() + expandedWidth &&
            mouseY >= itemY && mouseY < itemY + ENTRY_HEIGHT;

        if (isHovered) {
          g.fill(getX() + 1, itemY, getX() + expandedWidth - 1, itemY + ENTRY_HEIGHT, 0xFF333333);
        }

        g.drawString(font, options.get(index), getX() + 4, itemY + 3, 0xFFFFFF, false);
      }

      // Scrollbar - Aligned to EXPANDED WIDTH
      if (options.size() > maxVisible) {
        int scrollBarX = getX() + expandedWidth - 3;
        int trackHeight = listHeight;
        int barHeight = Math.max(4, (int)((float)maxVisible / options.size() * trackHeight));
        int barTop = startY + (int)((float)scroll / (options.size() - maxVisible) * (trackHeight - barHeight));

        g.fill(scrollBarX, barTop, scrollBarX + 2, barTop + barHeight, 0xFFAAAAAA);
      }

      // Clean up state
      RenderSystem.enableDepthTest();
      pose.popPose();
    }
  }

  @Override protected void updateWidgetNarration(NarrationElementOutput output) {}
  public String getSelected() { return currentOption; }
}