package org.openpatch.scratch.drawing;

import org.openpatch.scratch.Sprite;

/**
 * A sprite that draws itself with shapes and images instead of a costume: override
 * {@link #draw(Drawing)}. It is drawn in its layer behind the ordinary sprites of that layer.
 *
 * <pre>{@code
 * class Road extends DrawingSprite {
 *   public void draw(Drawing drawing) {
 *     drawing.rectangle(0, 0, 320, 180, new Color(90, 170, 220));
 *   }
 * }
 * }</pre>
 */
public class DrawingSprite extends Sprite {

  /**
   * A drawing sprite; it has no hitbox unless given one.
   *
   * @example.files DrawingSpriteExample.java
   */
  public DrawingSprite() {
    this.disableHitbox();
  }

  /**
   * Draws the sprite; called every frame on the game thread.
   *
   * @param drawing what to draw with, in the sprite's own coordinates
   *
   * @example.files DrawingSpriteExample.java
   */
  public void draw(Drawing drawing) {}
}
