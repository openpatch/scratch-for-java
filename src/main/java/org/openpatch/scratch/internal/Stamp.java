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

  public Stamp(Image image, double x2, double y2) {
    this(image, 0, x2, y2, RotationStyle.DONT);
  }

  public Stamp(Image image, double degrees, double x, double y, RotationStyle style) {
    this.image = image;
    this.x = x;
    this.y = y;
    this.style = style;
    this.degrees = degrees;
  }

  public void draw(PGraphics g) {
    g.push();
    g.imageMode(PConstants.CENTER);
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
        if (heading > -90 && heading < 90) {
          g.scale(1, 1);
        } else {
          g.scale(-1, 1);
        }
        break;
    }
    g.tint(
        (float) this.image.tint.getRed(),
        (float) this.image.tint.getGreen(),
        (float) this.image.tint.getBlue(),
        (float) this.image.transparency);
    // Draw at the costume's current size, not the file's natural size, so a
    // stamp matches the sprite it was taken from after setSize().
    g.image(this.image.originalImage, 0, 0, this.image.getWidth(), this.image.getHeight());
    g.noTint();
    g.pop();
  }
}
