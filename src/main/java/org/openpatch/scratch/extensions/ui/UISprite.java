package org.openpatch.scratch.extensions.ui;

import org.openpatch.scratch.Sprite;

/**
 * A sprite for buttons, bars and other parts of a user interface.
 *
 * <p>
 * It differs from a normal sprite in two ways. It is drawn on top of
 * everything else and stays put when the camera moves, and it can be given a
 * width and a height in pixels instead of only a size in percent. Together with
 * nine-slice scaling that lets one costume stretch to any size without the
 * corners smearing:
 *
 * <pre>{@code
 * public class Button extends UISprite {
 *   public Button() {
 *     this.addCostume("button", "ui/button.png");
 *     this.setNineSlice(12, 24, 12, 24);
 *     this.setWidth(600);
 *     this.setHeight(80);
 *   }
 * }
 * }</pre>
 */
public class UISprite extends Sprite {

  /** Creates a sprite that is drawn as part of the user interface. */
  public UISprite() {
    this.setUI(true);
  }

  /**
   * Sets the width of the sprite in pixels.
   *
   * @param width the width in pixels
   */
  @Override
  public void setWidth(double width) {
    super.setWidth(width);
  }

  /**
   * Sets the height of the sprite in pixels.
   *
   * @param height the height in pixels
   */
  @Override
  public void setHeight(double height) {
    super.setHeight(height);
  }

  /**
   * Changes the width of the sprite by the given number of pixels.
   *
   * @param amount how many pixels to add to the width
   */
  public void changeWidth(double amount) {
    this.setWidth(this.getWidth() + amount);
  }

  /**
   * Changes the height of the sprite by the given number of pixels.
   *
   * @param amount how many pixels to add to the height
   */
  public void changeHeight(double amount) {
    this.setHeight(this.getHeight() + amount);
  }

  /**
   * Keeps the corners and edges of the costume from stretching when the sprite
   * is resized, so that a button costume can be made any size.
   *
   * @param top    how many pixels of the costume belong to the top edge
   * @param right  how many pixels of the costume belong to the right edge
   * @param bottom how many pixels of the costume belong to the bottom edge
   * @param left   how many pixels of the costume belong to the left edge
   */
  @Override
  public void setNineSlice(int top, int right, int bottom, int left) {
    super.setNineSlice(top, right, bottom, left);
  }

  /** Lets the whole costume stretch again. */
  @Override
  public void disableNineSlice() {
    super.disableNineSlice();
  }
}
