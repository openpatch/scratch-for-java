package org.openpatch.scratch.internal.draw;

import org.openpatch.scratch.internal.text.TextLayout;

/**
 * A variable monitor as Scratch shows it: the label, then the value in an orange pill, in a box
 * whose top-left corner is at {@code (x, y)} (stage coordinates, y up). Drawn above the UI layer.
 */
public record MonitorDraw(TextLayout label, TextLayout value, double x, double y) {

  /** Height of a monitor box, and the step between stacked ones, in stage units. */
  public static final double HEIGHT = 24, STEP = 28;
}
