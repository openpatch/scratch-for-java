package org.openpatch.scratch.internal.draw;

/**
 * The camera: the world point {@code (x, y)} is drawn at the stage's centre, and the world is
 * scaled by {@code zoom / 100} around it. The identity is {@code (0, 0, 100)}.
 */
public record CameraDraw(double x, double y, double zoom) {

  public static final CameraDraw IDENTITY = new CameraDraw(0, 0, 100);
}
