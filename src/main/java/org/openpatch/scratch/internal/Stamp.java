package org.openpatch.scratch.internal;

import org.openpatch.scratch.RotationStyle;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class Stamp {

  public Image image;
  private double x;
  private double y;
  private RotationStyle style;
  private double degrees;
  // the point of the image at (x, y), in drawn pixels from its top left corner
  private double centerX;
  private double centerY;
  // Tiled's flip flags: mirrored across the diagonal first, then horizontally/vertically
  private boolean flipHorizontal;
  private boolean flipVertical;
  private boolean flipDiagonal;

  public Stamp(Image image, double x2, double y2) {
    this(image, 0, x2, y2, RotationStyle.DONT);
  }

  /**
   * A map tile, possibly flipped the way Tiled stores it.
   *
   * @param image      the tile
   * @param x          the x position
   * @param y          the y position
   * @param horizontal mirrored left/right
   * @param vertical   mirrored up/down
   * @param diagonal   mirrored across the diagonal (with the others: turned)
   */
  public Stamp(Image image, double x, double y, boolean horizontal, boolean vertical,
      boolean diagonal) {
    this(image, 0, x, y, RotationStyle.DONT);
    this.flipHorizontal = horizontal;
    this.flipVertical = vertical;
    this.flipDiagonal = diagonal;
  }

  public Stamp(Image image, double degrees, double x, double y, RotationStyle style) {
    this(image, degrees, x, y, style, image.getWidth() / 2.0, image.getHeight() / 2.0);
  }

  public Stamp(Image image, double degrees, double x, double y, RotationStyle style,
      double centerX, double centerY) {
    this.image = image;
    this.x = x;
    this.y = y;
    this.style = style;
    this.degrees = degrees;
    this.centerX = centerX;
    this.centerY = centerY;
  }

  public void draw(PGraphics g) {
    g.push();
    g.imageMode(PConstants.CORNER);
    g.translate((float) this.x, (float) -this.y);
    // A heading of 90 points right, which is where the artwork already faces.
    // Kept in a local: draw() must not mutate the stamp, or a stamp that is
    // drawn more than once would keep turning.
    double heading = this.degrees - 90;
    switch (this.style) {
      case DONT:
        break;
      case ALL_AROUND:
        g.rotate(PApplet.radians((float) heading));
        break;
      case LEFT_RIGHT:
        if (Image.isMirrored(this.degrees, this.style)) {
          g.scale(-1, 1);
        }
        break;
    }
    if (this.flipHorizontal || this.flipVertical || this.flipDiagonal) {
      // the last transform applies first: transpose, then the mirror flips
      g.scale(this.flipHorizontal ? -1 : 1, this.flipVertical ? -1 : 1);
      if (this.flipDiagonal) {
        g.applyMatrix(0, 1, 0, 1, 0, 0);
      }
    }
    g.tint(
        (float) this.image.tint.getRed(),
        (float) this.image.tint.getGreen(),
        (float) this.image.tint.getBlue(),
        this.image.alpha());
    // Draw at the costume's current size, not the file's natural size, so a
    // stamp matches the sprite it was taken from after setSize().
    g.image(this.image.originalImage, (float) -this.centerX, (float) -this.centerY,
        this.image.getWidth(), this.image.getHeight());
    g.noTint();
    g.pop();
  }
}
