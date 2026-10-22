package org.openpatch.scratch.internal.draw;

/**
 * A polygon outline or filled polygon in stage coordinates, e.g. a debug hitbox.
 *
 * @param points {@code x0, y0, x1, y1, …}, closed
 * @param fill ARGB fill, 0 for none
 * @param stroke ARGB outline, 0 for none
 * @param strokeWeight outline width in stage pixels
 */
public record ShapeDraw(double[] points, int fill, int stroke, double strokeWeight) implements Item {}
