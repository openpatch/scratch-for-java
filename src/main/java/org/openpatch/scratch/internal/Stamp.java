package org.openpatch.scratch.internal;

import org.openpatch.scratch.Origin;
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
  private Origin origin;
  private double originX;
  private double originY;

  public Stamp(Image image, double x2, double y2) {
    this(image, 0, x2, y2, RotationStyle.DONT, Origin.CENTER, 0, 0);
  }

  public Stamp(Image image, double degrees, double x, double y, RotationStyle style) {
    this(image, degrees, x, y, style, Origin.CENTER, 0, 0);
  }

  public Stamp(
      Image image,
      double degrees,
      double x,
      double y,
      RotationStyle style,
      Origin origin,
      double originX,
      double originY) {
    this.image = image;
    this.x = x;
    this.y = y;
    this.style = style;
    this.degrees = degrees;
    this.origin = origin;
    this.originX = originX;
    this.originY = originY;
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

    // Calculate origin offset based on origin mode
    float offsetX = 0;
    float offsetY = 0;
    int width = this.image.originalImage.width;
    int height = this.image.originalImage.height;

    switch (this.origin) {
      case TOP_LEFT:
        offsetX = width / 2.0f;
        offsetY = -height / 2.0f;
        break;
      case TOP_CENTER:
        offsetX = 0;
        offsetY = -height / 2.0f;
        break;
      case TOP_RIGHT:
        offsetX = -width / 2.0f;
        offsetY = -height / 2.0f;
        break;
      case CENTER_LEFT:
        offsetX = width / 2.0f;
        offsetY = 0;
        break;
      case CENTER:
        offsetX = 0;
        offsetY = 0;
        break;
      case CENTER_RIGHT:
        offsetX = -width / 2.0f;
        offsetY = 0;
        break;
      case BOTTOM_LEFT:
        offsetX = width / 2.0f;
        offsetY = height / 2.0f;
        break;
      case BOTTOM_CENTER:
        offsetX = 0;
        offsetY = height / 2.0f;
        break;
      case BOTTOM_RIGHT:
        offsetX = -width / 2.0f;
        offsetY = height / 2.0f;
        break;
      case CUSTOM:
        // For custom origin, apply the offset from center
        offsetX = (float) -this.originX;
        offsetY = (float) this.originY;
        break;
    }

    g.translate(offsetX, offsetY);
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
