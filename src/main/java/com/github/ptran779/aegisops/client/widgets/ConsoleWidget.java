package com.github.ptran779.aegisops.client.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ConsoleWidget extends AbstractWidget {

  // RAW STORAGE: Just the visual text shape. No objects, no extra data.
  private final List<FormattedCharSequence> lines = new ArrayList<>();
  private final Font font;

  // THE ONE COLOR (set in constructor)
  private final int consoleColor;

  private int maxLines;
  private static final int PADDING = 4;
  private static final int LINE_HEIGHT = 10;

  private int scrollOffset = 0;
  private boolean autoScroll = true;

  // CONSTRUCTOR: Set the color HERE
  public ConsoleWidget(int x, int y, int width, int height, Font font, int hexColor) {
    super(x, y, width, height, Component.empty());
    this.font = font;
    this.consoleColor = hexColor; // <--- The global color for this console
    this.maxLines = 100; // Default cap
  }

  public void log(String message) {
    if (message == null) return;
    // 1. Prefix >
    String fullText = "> " + message;
    // 2. Wrap (Heavy math done ONCE)
    // We use Component.literal just to bridge to the font splitter
    List<FormattedCharSequence> sequences = this.font.split(Component.literal(fullText), this.width - (PADDING * 2));
    // 3. Store raw sequence
    this.lines.addAll(sequences);
    // 4. Cap Memory
    while (this.lines.size() > maxLines) {
      this.lines.remove(0);
    }
    if (this.autoScroll) {
      scrollToBottom();
    }
  }

  public void clear() {
    this.lines.clear();
    this.scrollOffset = 0;
  }

  public void setMaxLines(int max) {
    this.maxLines = max;
  }

  private void scrollToBottom() {
    int contentHeight = this.lines.size() * LINE_HEIGHT;
    int viewHeight = this.height - (PADDING * 2);
    this.scrollOffset = Math.max(0, contentHeight - viewHeight);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (!isMouseOver(mouseX, mouseY)) return false;

    int contentHeight = this.lines.size() * LINE_HEIGHT;
    int viewHeight = this.height - (PADDING * 2);
    int maxScroll = Math.max(0, contentHeight - viewHeight);

    this.scrollOffset -= (int) (delta * LINE_HEIGHT * 2);
    this.scrollOffset = Mth.clamp(this.scrollOffset, 0, maxScroll);
    this.autoScroll = (this.scrollOffset >= maxScroll);

    return true;
  }

  @Override
  protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    // Border
    g.renderOutline(getX(), getY(), width, height, 0xFF444444);

    // Scissor
    g.enableScissor(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1);

    int viewHeight = this.height - (PADDING * 2);
    int startX = getX() + PADDING;
    int startY = getY() + PADDING;

    for (int i = 0; i < lines.size(); i++) {
      int lineY = (i * LINE_HEIGHT) - scrollOffset;

      if (lineY + LINE_HEIGHT >= 0 && lineY < viewHeight) {
        // RENDER: Use the single global color for everything
        g.drawString(font, lines.get(i), startX, startY + lineY, this.consoleColor, true);
      }
    }

    g.disableScissor();
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}