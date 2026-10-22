package org.openpatch.scratch.internal.draw;

import org.openpatch.scratch.internal.text.TextLayout;

/**
 * Laid-out text at a place on the stage, with its box. Lines are offset within the box by
 * {@code align}; glyph positions come from the layout, in stage units at the nominal size.
 *
 * @param layout lines and glyphs
 * @param x left edge of the box in stage coordinates
 * @param y top edge of the box in stage coordinates (y up: the box extends downwards)
 * @param width width of the box
 * @param height height of the box
 * @param align 0 left, 1 centre, 2 right: how lines sit within the box, inside the padding
 * @param color ARGB of the letters
 * @param box how the box is drawn: none, a plain rounded box, or a speech or thought bubble
 * @param boxColor ARGB fill of the box
 * @param boxStroke ARGB outline of the box
 * @param padding space between the box's edge and the letters
 * @param tailX for bubbles: the x the tail starts from (the speaking sprite's corner); a tail in
 *     the right half of the box points the other way (the bubble is on the sprite's left)
 */
public record TextDraw(TextLayout layout, double x, double y, double width, double height, int align, int color,
    Box box, int boxColor, int boxStroke, double padding, double tailX) implements Item {

  /** The box around a text. */
  public enum Box {
    NONE,
    PLAIN,
    SPEECH,
    THOUGHT
  }
}
