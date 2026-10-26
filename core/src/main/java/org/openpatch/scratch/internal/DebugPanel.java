package org.openpatch.scratch.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.openpatch.scratch.internal.draw.Item;
import org.openpatch.scratch.internal.draw.ShapeDraw;
import org.openpatch.scratch.internal.draw.TextDraw;
import org.openpatch.scratch.internal.text.Font;
import org.openpatch.scratch.internal.text.TextLayout;

/**
 * The debug mode panel in the bottom right corner: a chip that opens a list of checkboxes, one
 * per {@link DebugOption}. Clicks on it never reach the learner ({@code Game} asks
 * {@link #press} first and drops the event); its items go out with the draw list's
 * {@code overlayItems}, so captures stay without it. Everything here runs on the game thread.
 */
public final class DebugPanel {

  private static final double FONT_SIZE = 12;
  private static final double MARGIN = 6;
  private static final double PAD = 6;
  private static final double ROW = 18;
  private static final double BOX = 9;
  private static final int BACKGROUND = 0xd0121212;
  private static final int BORDER = 0xff8a8a8a;
  private static final int TEXT = 0xffffffff;
  private static final int CHECKED = 0x66ff4444;

  private static final DebugOption[] OPTIONS = DebugOption.values();

  private final Font font = Font.defaultFont();
  private final TextLayout chip = TextLayout.of("Debug", this.font, FONT_SIZE, 0);
  private final double labelWidth = this.widestLabel();
  private final double width = PAD + BOX + 6 + this.labelWidth + PAD;
  private final double height = PAD + ROW * (OPTIONS.length + 1) + PAD;
  private final double chipWidth = this.chip.width() + 2 * PAD;
  private final double chipHeight = this.chip.height() + 2 * PAD;

  /** Whether the list of checkboxes stands open. */
  private boolean open;

  /**
   * A mouse press at a stage coordinate: opens and closes the panel and ticks its checkboxes.
   *
   * @return whether the press was on the panel and must not reach the learner
   */
  public boolean press(double x, double y, int stageWidth, int stageHeight, Set<DebugOption> options) {
    if (!this.open) {
      if (this.inside(x, y, this.chipLeft(stageWidth), this.bottom(stageHeight), this.chipWidth,
          this.chipHeight)) {
        this.open = true;
        return true;
      }
      return false;
    }
    if (!this.inside(x, y, this.left(stageWidth), this.bottom(stageHeight), this.width, this.height)) {
      return false;
    }
    // the row the press lands in, 0 the title that closes the panel again
    int row = (int) ((this.bottom(stageHeight) + this.height - y - PAD) / ROW);
    if (row <= 0) {
      this.open = false;
    } else if (row <= OPTIONS.length) {
      DebugOption option = OPTIONS[row - 1];
      if (!options.remove(option)) {
        options.add(option);
      }
    }
    return true;
  }

  /** The panel's items for the draw list's {@code overlayItems}. */
  public List<Item> items(int stageWidth, int stageHeight, Set<DebugOption> options) {
    if (!this.open) {
      return List.of(new TextDraw(this.chip, this.chipLeft(stageWidth),
          this.bottom(stageHeight) + this.chipHeight, this.chipWidth, this.chipHeight, 0, TEXT,
          TextDraw.Box.PLAIN, BACKGROUND, BORDER, PAD, 0));
    }
    double left = this.left(stageWidth), bottom = this.bottom(stageHeight);
    List<Item> items = new ArrayList<>();
    items.add(new ShapeDraw(new double[] {left, bottom, left + this.width, bottom, left + this.width,
        bottom + this.height, left, bottom + this.height}, BACKGROUND, BORDER, 1));
    double ascent = this.font.ascent(FONT_SIZE), descent = this.font.descent(FONT_SIZE);
    for (int row = 0; row <= OPTIONS.length; row++) {
      double centre = bottom + this.height - PAD - ROW * row - ROW / 2;
      if (row == 0) {
        items.add(this.text("Debug", left + PAD, centre, ascent, descent));
        continue;
      }
      DebugOption option = OPTIONS[row - 1];
      double bx = left + PAD, by = centre - BOX / 2;
      items.add(new ShapeDraw(new double[] {bx, by, bx + BOX, by, bx + BOX, by + BOX, bx, by + BOX},
          options.contains(option) ? CHECKED : 0, 0xffffffff, 1));
      items.add(this.text(option.label, bx + BOX + 6, centre, ascent, descent));
    }
    return items;
  }

  /**
   * A text item centred on a row. {@code TextDraw} wants the box's top edge, and the letters hang
   * downwards from it by {@code ascent + descent} ({@code Font.descent} is positive), so the top
   * edge lies half the ink height above the row's centre — the way {@code Stage}'s debug texts
   * centre theirs.
   */
  private TextDraw text(String label, double x, double centre, double ascent, double descent) {
    TextLayout layout = TextLayout.of(label, this.font, FONT_SIZE, 0);
    return new TextDraw(layout, x, centre + (ascent + descent) / 2, layout.width(), layout.height(), 0, TEXT,
        TextDraw.Box.NONE, 0, 0, 0, 0);
  }

  private double left(int stageWidth) {
    return stageWidth / 2.0 - MARGIN - this.width;
  }

  private double chipLeft(int stageWidth) {
    return stageWidth / 2.0 - MARGIN - this.chipWidth;
  }

  /** The panel's and the chip's bottom edge, above the stage's bottom margin. */
  private double bottom(int stageHeight) {
    return -stageHeight / 2.0 + MARGIN;
  }

  private boolean inside(double x, double y, double left, double bottom, double w, double h) {
    return x >= left && x <= left + w && y >= bottom && y <= bottom + h;
  }

  private double widestLabel() {
    double widest = this.chip.width();
    for (DebugOption option : OPTIONS) {
      widest = Math.max(widest, TextLayout.of(option.label, this.font, FONT_SIZE, 0).width());
    }
    return widest;
  }
}
