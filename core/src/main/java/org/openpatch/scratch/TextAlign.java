package org.openpatch.scratch;

/**
 * Where a piece of text sits relative to its position.
 *
 * <pre>{@code
 * myText.setAlign(TextAlign.LEFT);
 * }</pre>
 *
 * @see Text#setAlign(TextAlign)
 */
public enum TextAlign {

  /** Whatever the text style already uses. */
  DEFAULT,

  /** Centred on its position. */
  CENTER,

  /** Its right edge at its position. */
  RIGHT,

  /** Its left edge at its position. */
  LEFT
}
