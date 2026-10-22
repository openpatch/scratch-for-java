package org.openpatch.scratch.internal.draw;

/**
 * Starts or ends clipping: items after it are drawn only inside the rectangle (layer
 * coordinates, y up, axis-aligned on the screen) until a ClipDraw with {@code active} false.
 */
public record ClipDraw(boolean active, double left, double bottom, double right, double top) implements Item {

  public static final ClipDraw END = new ClipDraw(false, 0, 0, 0, 0);
}
