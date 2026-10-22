package org.openpatch.scratch.internal.draw;

import org.openpatch.scratch.internal.text.TextLayout;

/**
 * The ask box as Scratch shows it: a white card along the bottom of the stage with the question
 * (when the stage asks) above a rounded answer field that ends in a round check button. In stage
 * coordinates (y up); {@code (x, y)} is the card's top-left corner. Drawn above the UI layer.
 *
 * @param question the question's lines, or null when a sprite asks (its bubble shows the question)
 * @param answer what has been typed, as far as it fits into the field
 * @param x left edge of the card
 * @param y top edge of the card
 * @param width width of the card
 * @param height height of the card
 * @param caret whether the blinking caret is shown in this frame
 */
public record AskDraw(TextLayout question, TextLayout answer, double x, double y, double width, double height,
    boolean caret) {

  /** Scratch's measures (scratch-gui's question box), in stage units. */
  public static final double MARGIN = 8, PADDING = 16, GAP = 8, FIELD = 32, INSET = 12, BUTTON = 24,
      BUTTON_INSET = 4;

  /** @return the height of a card for a question of this many stage units, 0 for none */
  public static double heightFor(double questionHeight) {
    return 2 * PADDING + FIELD + (questionHeight > 0 ? questionHeight + GAP : 0);
  }

  /** @return the answer field's left edge */
  public double fieldX() {
    return this.x + PADDING;
  }

  /** @return the answer field's top edge */
  public double fieldY() {
    return this.y - this.height + PADDING + FIELD;
  }

  /** @return the answer field's width */
  public double fieldWidth() {
    return this.width - 2 * PADDING;
  }

  /** @return the centre of the check button, x */
  public double buttonX() {
    return this.fieldX() + this.fieldWidth() - BUTTON_INSET - BUTTON / 2;
  }

  /** @return the centre of the check button, y */
  public double buttonY() {
    return this.fieldY() - FIELD / 2;
  }

  /** @return whether a point (stage coordinates) is on the check button */
  public boolean onButton(double px, double py) {
    return Math.hypot(px - this.buttonX(), py - this.buttonY()) <= BUTTON / 2;
  }
}
