package com.github.ptran779.aegisops.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DropDownWidget extends AbstractWidget {
  private final List<String> options;
  private final Consumer<String> onSelect;

  private boolean open = false;
  private int scroll = 0;
  private String currentOption = "";

  private static final int MAX_VISIBLE = 6;
  private static final int ENTRY_HEIGHT = 14;

  public DropDownWidget(int x, int y, int width, int height, Iterable<String> options, String currentOption, Consumer<String> onSelect) {
    super(x, y, width, height, Component.literal("Select Skin"));
    this.options = new ArrayList<>();
    options.forEach(this.options::add);
    this.currentOption = currentOption;
    this.onSelect = onSelect;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (button != 0) return false; // only left click
    onClick(mouseX, mouseY);
    return true; // consume click
  }
  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (!open) return false;           // only scroll when dropdown is open
    if (options.size() <= MAX_VISIBLE) return false; // no need to scroll

    int startY = getY() + height;
    int endY = startY + MAX_VISIBLE * ENTRY_HEIGHT;

    // only scroll if mouse is over dropdown list
    if (mouseX < getX() || mouseX > getX() + width ||
        mouseY < startY || mouseY > endY) {
      return false;
    }

    // delta > 0 means scroll up, < 0 scroll down
    scroll -= Math.signum(delta);  // ±1
    scroll = Math.max(0, Math.min(scroll, options.size() - MAX_VISIBLE));

    return true; // indicate the event was handled
  }

  @Override
  public void onClick(double mouseX, double mouseY) {
    // Check if clicked inside the main dropdown button
    if (mouseX >= getX() && mouseX <= getX() + width &&
        mouseY >= getY() && mouseY <= getY() + height) {
      open = !open; // toggle open/close
      return;
    }

    // If dropdown is open, check if clicked inside the options
    if (open) {
      int startY = getY() + height;
      int endY = startY + MAX_VISIBLE * ENTRY_HEIGHT;

      if (mouseX >= getX() && mouseX <= getX() + width &&
          mouseY >= startY && mouseY <= endY) {
        int idx = (int)((mouseY - startY) / ENTRY_HEIGHT) + scroll;
        if (idx >= 0 && idx < options.size()) {
          onSelect.accept(options.get(idx)); // trigger callback
          currentOption = options.get(idx);
          open = false;
        }
      } else {
        // clicking outside closes
        open = false;
      }
    }
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

  }

  protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float delta) {
    // main button background
    g.fill(getX(), getY(), getX() + width, getY() + height, 0xFF555555);
    g.drawString(Minecraft.getInstance().font, currentOption, getX() + 2, getY() + 2, 0xFFFFFF);

    if (!open) return;

    int startY = getY() + height;

    // dropdown background
    g.fill(getX(), startY, getX() + width, startY + MAX_VISIBLE * ENTRY_HEIGHT, 0xFF333333);

    // visible items
    for (int i = 0; i < MAX_VISIBLE; i++) {
      int idx = scroll + i;
      if (idx >= options.size()) break;

      int y = startY + i * ENTRY_HEIGHT;
      g.drawString(Minecraft.getInstance().font, options.get(idx), getX() + 2, y + 3, 0xFFFFFF);
    }

    // scroll icons
    if (options.size() > MAX_VISIBLE) {
      int arrowX = getX() + width + 3;

      g.drawString(Minecraft.getInstance().font, "▲", arrowX, startY, 0xFFFFFF);
      g.drawString(Minecraft.getInstance().font, "▼", arrowX, startY + MAX_VISIBLE * ENTRY_HEIGHT - 10, 0xFFFFFF);
    }
  }
}
