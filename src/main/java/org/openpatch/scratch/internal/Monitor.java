package org.openpatch.scratch.internal;

import java.util.function.Supplier;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * A variable monitor, drawn like Scratch's: a light box with the variable's
 * name and its value in an orange pill. The value is asked for every frame, so
 * the monitor always shows what the variable holds right now.
 *
 * <p>
 * Nothing outside the library should use this; stages and sprites show them
 * with {@code showVariable(...)}.
 */
public final class Monitor {

  /** Height of one monitor, gap included: where the next one starts. */
  public static final float HEIGHT = 28;

  private final String label;
  private final Supplier<?> value;

  /**
   * @param label what the monitor says, for example "Cat: score"
   * @param value asked for the value every frame
   */
  public Monitor(String label, Supplier<?> value) {
    this.label = label;
    this.value = value;
  }

  /** The monitor's text. */
  public String getLabel() {
    return this.label;
  }

  /** The current value as the monitor shows it. */
  public String getText() {
    Object current;
    try {
      current = this.value.get();
    } catch (RuntimeException e) {
      return "?";
    }
    return format(current);
  }

  /** Numbers without a needless ".0", like Scratch shows them. */
  static String format(Object value) {
    if (value == null) return "";
    if (value instanceof Double || value instanceof Float) {
      double d = ((Number) value).doubleValue();
      if (d == Math.rint(d) && Math.abs(d) < 1e15) return String.valueOf((long) d);
      return String.valueOf(Math.round(d * 100) / 100.0);
    }
    if (value.getClass().isArray()) {
      if (value instanceof int[] a) return java.util.Arrays.toString(a);
      if (value instanceof double[] a) return java.util.Arrays.toString(a);
      if (value instanceof Object[] a) return java.util.Arrays.toString(a);
    }
    return String.valueOf(value);
  }

  /**
   * Draws the monitor with its top left corner at (x, y) in the buffer's
   * coordinates.
   */
  public void draw(PGraphics g, float x, float y) {
    String text = this.getText();
    g.pushStyle();
    g.rectMode(PConstants.CORNER);
    g.textFont(Font.getDefaultFont(), 12);
    float labelWidth = g.textWidth(this.label);
    float valueWidth = Math.max(30, g.textWidth(text) + 14);
    g.stroke(195, 204, 217);
    g.strokeWeight(1);
    g.fill(230, 240, 255, 235);
    g.rect(x, y, labelWidth + valueWidth + 18, 24, 4);
    g.noStroke();
    g.fill(87, 94, 117);
    g.textAlign(PConstants.LEFT, PConstants.CENTER);
    g.text(this.label, x + 6, y + 11);
    g.fill(255, 140, 26);
    g.rect(x + labelWidth + 12, y + 3, valueWidth, 18, 9);
    g.fill(255);
    g.textAlign(PConstants.CENTER, PConstants.CENTER);
    g.text(text, x + labelWidth + 12 + valueWidth / 2, y + 11);
    g.popStyle();
  }
}
