package org.openpatch.scratch.internal.draw;

import org.openpatch.scratch.internal.image.Image;

/**
 * A region of an image on any quad, for extensions drawing through the Canvas API (map tiles,
 * bitmap glyphs, drawings). Consecutive image draws of the same image are drawn together.
 *
 * @param image the image
 * @param sx the region's left edge in image pixels
 * @param sy its top edge
 * @param sw its width
 * @param sh its height
 * @param corners the quad in layer coordinates (y up): top-left, top-right, bottom-right,
 *     bottom-left as {@code x0, y0, …, x3, y3}
 * @param opacity 0 invisible … 1 opaque
 */
public record ImageDraw(Image image, int sx, int sy, int sw, int sh, double[] corners, float opacity)
    implements Item {}
