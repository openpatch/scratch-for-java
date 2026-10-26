package org.openpatch.scratch.internal.draw;

import java.util.List;

/**
 * One layer: its canvas (fixed on screen, unaffected by the camera) and then its items, back to
 * front.
 *
 * @param index {@code Layer.ordinal()}
 * @param canvas whether the layer has a canvas at all (created on first use of a pen or stamp)
 * @param canvasChanges the canvas regions changed since the previous draw list, oldest first; the
 *     renderer uploads them in this order
 * @param items sprites and shapes, back to front
 * @param world whether the camera applies (every layer but UI)
 */
public record LayerDraw(int index, boolean canvas, List<CanvasRegion> canvasChanges, List<Item> items,
    boolean world) {}
