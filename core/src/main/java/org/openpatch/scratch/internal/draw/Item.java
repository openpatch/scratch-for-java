package org.openpatch.scratch.internal.draw;

/** Something a layer draws: a sprite, a shape, an image region, a text, or a clip. */
public sealed interface Item permits SpriteDraw, ShapeDraw, ImageDraw, TextDraw, ClipDraw {}
