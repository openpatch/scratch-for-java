package org.openpatch.scratch.extensions.drawing;

import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.internal.Image;
import processing.core.PGraphics;

/**
 * A sprite whose appearance is drawn in {@link #draw(Drawing)}. Movement,
 * visibility, size, direction, transparency, layering and the stage camera
 * work as for other sprites. Choose colours in draw() instead of adding costumes.
 * It starts without a hitbox; give it a shape and enableHitbox() if needed.
 *
   * @example.files DrawingSpriteExample.java
 */
public class DrawingSprite extends Sprite {
  private double transparency;

  /** Creates a custom drawing sprite.
   * @example.files DrawingSpriteExample.java
   */
  public DrawingSprite() { this.disableHitbox(); }

  /**
   * Draws this frame's appearance. Override this method; use run() for changes
   * to game state, since a paused game can still be redrawn.
   * @param drawing this frame's canvas in local Scratch coordinates
   *
   * @example.files DrawingSpriteExample.java
   */
  public void draw(Drawing drawing) {}

  @Override
  protected final void draw(PGraphics buffer) {
    if (!this.isVisible()) return;
    buffer.push();
    Drawing drawing = new Drawing(buffer, this.transparency);
    try {
      buffer.translate((float) this.getX(), (float) -this.getY());
      if (this.getRotationStyle() == RotationStyle.ALL_AROUND) {
        buffer.rotate((float) Math.toRadians(this.getDirection() - 90));
      } else if (Image.isMirrored(this.getDirection(), this.getRotationStyle())) {
        buffer.scale(-1, 1);
      }
      buffer.scale((float) (this.getSize() / 100));
      this.draw(drawing);
    } finally {
      drawing.finish();
      buffer.noClip();
      buffer.pop();
    }
  }

  /**
   * Chooses whether the drawing stays fixed on screen as UI.
   * @param isUI true for UI, false for the game world
   *
   * @example.files DrawingSpriteExample.java
   */
  public void setIsUI(boolean isUI) { this.setUI(isUI); }

  /** @param percent 0 solid, 100 invisible
   *
   * @example.files DrawingSpriteExample.java
   */
  @Override
  public void setTransparency(double percent) {
    if (!Double.isFinite(percent)) throw new IllegalArgumentException("Transparency must be finite.");
    this.transparency = Math.max(0, Math.min(100, percent));
  }

  /** @param step change in percentage points
   *
   * @example.files DrawingSpriteExample.java
   */
  @Override
  public void changeTransparency(double step) { this.setTransparency(this.transparency + step); }

  /**
   * @return transparency in percent
   *
   * @example.files DrawingSpriteExample.java
   */
  @Override
  public double getTransparency() { return this.transparency; }
}
