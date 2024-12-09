package org.openpatch.scratch.extensions.text;

import processing.core.PApplet;

/**
 * The TextAlign class provides constants for text alignment options. These constants can be used to
 * set the alignment of text in a graphical application.
 *
 * <p>Available alignment options:
 *
 * <ul>
 *   <li>{@link #DEFAULT} - Default alignment, represented by -1.
 *   <li>{@link #CENTER} - Center alignment, uses PApplet.CENTER.
 *   <li>{@link #RIGHT} - Right alignment, uses PApplet.RIGHT.
 *   <li>{@link #LEFT} - Left alignment, uses PApplet.LEFT.
 * </ul>
 */
public class TextAlign {
  public static int DEFAULT = -1;
  public static int CENTER = PApplet.CENTER;
  public static int RIGHT = PApplet.RIGHT;
  public static int LEFT = PApplet.LEFT;
}
