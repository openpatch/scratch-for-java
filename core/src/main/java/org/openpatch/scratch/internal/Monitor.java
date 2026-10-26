package org.openpatch.scratch.internal;

import java.util.function.Supplier;
import org.openpatch.scratch.internal.draw.MonitorDraw;
import org.openpatch.scratch.internal.text.Font;
import org.openpatch.scratch.internal.text.TextLayout;

/** A variable shown on the stage, read when the draw list is made (on the game thread). */
public final class Monitor {

  private static final double SIZE = 12;

  private final String label;
  private final Supplier<?> value;

  public Monitor(String label, Supplier<?> value) {
    this.label = label;
    this.value = value;
  }

  /** The value as text: whole doubles without ".0", others to two places; "?" if reading fails. */
  public String text() {
    Object current;
    try {
      current = this.value.get();
    } catch (RuntimeException e) {
      return "?";
    }
    return format(current);
  }

  static String format(Object value) {
    if (value == null) {
      return "";
    }
    if (value instanceof Double || value instanceof Float) {
      double d = ((Number) value).doubleValue();
      if (d == Math.rint(d) && Math.abs(d) < 1e15) {
        return String.valueOf((long) d);
      }
      return String.valueOf(Math.round(d * 100) / 100.0);
    }
    if (value instanceof int[] a) {
      return java.util.Arrays.toString(a);
    }
    if (value instanceof double[] a) {
      return java.util.Arrays.toString(a);
    }
    if (value instanceof Object[] a) {
      return java.util.Arrays.toString(a);
    }
    return String.valueOf(value);
  }

  public MonitorDraw toDraw(Font font, double x, double y) {
    return new MonitorDraw(TextLayout.of(this.label, font, SIZE, 0), TextLayout.of(this.text(), font, SIZE, 0), x, y);
  }
}
