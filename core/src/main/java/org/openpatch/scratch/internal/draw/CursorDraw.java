package org.openpatch.scratch.internal.draw;

import org.openpatch.scratch.internal.image.Image;

/** A custom mouse pointer: the image and the pixel of it that clicks. */
public record CursorDraw(Image image, int hotX, int hotY) {}
