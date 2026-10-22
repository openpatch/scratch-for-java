package org.openpatch.scratch.internal.draw;

/**
 * A copy of a changed part of a layer's canvas: {@code argb} holds {@code width × height}
 * pixels, top row first, straight alpha. {@code x, y} is its top-left corner in canvas pixels
 * (y down, as the canvas stores them).
 */
public record CanvasRegion(int x, int y, int width, int height, int[] argb) {}
