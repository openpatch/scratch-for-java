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
    this.degrees -= 90;
    switch (this.style) {
      case DONT:
        break;
      case ALL_AROUND:
        g.rotate(PApplet.radians((float) this.degrees));
        break;
      case LEFT_RIGHT:
        if (this.degrees > -90 && this.degrees < 90) {
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
    g.image(this.image.originalImage, 0, 0);
    g.noTint();
    g.pop();
  }
}
