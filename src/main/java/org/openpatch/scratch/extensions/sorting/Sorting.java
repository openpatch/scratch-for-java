package org.openpatch.scratch.extensions.sorting;

import java.util.Comparator;

import org.openpatch.scratch.Sprite;

/**
 * Decides the order in which the sprites of a stage are drawn.
 *
 * <p>
 * Reached through {@code stage.getSorting()}:
 *
 * <pre>{@code
 * this.getSorting().byY();
 * }</pre>
 */
public class Sorting {

  private Comparator<? super Sprite> comparator;

  /**
   * Draws sprites that are further down the stage in front of sprites that are
   * further up, which is what a view from above usually wants.
   */
  public void byY() {
    this.comparator = (a, b) -> (int) ((b.getY() - b.getHeight() / 2) - (a.getY() - a.getHeight() / 2));
  }

  /**
   * Draws sprites in an order of your own.
   *
   * @param comparator decides which of two sprites is drawn first
   */
  public void by(Comparator<? super Sprite> comparator) {
    this.comparator = comparator;
  }

  /** Draws sprites in the order they were added, which is the default. */
  public void off() {
    this.comparator = null;
  }

  /**
   * Checks whether the sprites are being sorted.
   *
   * @return true if an order is set
   */
  public boolean isOn() {
    return this.comparator != null;
  }

  /**
   * Returns the order currently used.
   *
   * @return the comparator, or null if the sprites are not sorted
   */
  public Comparator<? super Sprite> getComparator() {
    return this.comparator;
  }
}
