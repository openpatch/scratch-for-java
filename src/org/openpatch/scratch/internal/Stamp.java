package org.openpatch.scratch.internal;

import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.Window;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class Stamp {

  public Image image;
  private float x;
  private float y;
  private RotationStyle style;
  private float degrees;

  public Stamp(Image image, double x2, double y2) {
    this(image, 0, x2, y2, RotationStyle.DONT);
  }

  public Stamp(Image image, double degrees, double x, double y, RotationStyle style) {
    this.image = image;
    this.x = (float) x;
    this.y = (float) y;
    this.style = style;
    this.degrees = (float) degrees;
  }

  public void draw(PGraphics g) {
    g.push();
    g.imageMode(PConstants.CENTER);
    g.translate(
        this.x + Window.getInstance().getWidth() / 2,
        -this.y + Window.getInstance().getHeight() / 2);
    this.degrees -= 90;
    switch (this.style) {
      case DONT:
        break;
      case ALL_AROUND:
        g.rotate(PApplet.radians(this.degrees));
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
        this.image.tint.getRed(),
        this.image.tint.getGreen(),
        this.image.tint.getBlue(),
        this.image.transparency);
    g.image(this.image.image, 0, 0);
    g.noTint();
    g.pop();
  }
}
