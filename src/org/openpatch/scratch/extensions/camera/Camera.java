package org.openpatch.scratch.extensions.camera;

import org.openpatch.scratch.extensions.math.Vector2;

public class Camera {
  private double x;
  private double y;
  private double zoom;
  private double zoomLimitH;
  private double zoomLimitL;

  public Camera() {
    this.x = 0;
    this.y = 0;
    this.zoom = 100;
    this.zoomLimitH = 200;
    this.zoomLimitL = 50;
  }

  /**
   * Sets the position of the sprite
   *
   * @param x a x coordinate
   * @param y a y coordinate
   */
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Sets the position of the sprite based on the coordinates of a given vector.
   *
   * @param v a vector
   */
  public void setPosition(Vector2 v) {
    this.setPosition(v.getX(), v.getY());
  }

  public Vector2 getPosition() {
    return new Vector2(x, y);
  }

  /**
   * Returns the x coordinate of the sprite
   *
   * @return a x coordinate
   */
  public double getX() {
    return this.x;
  }

  /**
   * Sets the x coordinate
   *
   * @param x a x coordinate
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Changes x by a certain amount
   *
   * @param x number in pixels
   */
  public void changeX(double x) {
    this.setX(this.getX() + y);
  }

  /**
   * Returns the y coordinate of the sprite
   *
   * @return a y coordinate
   */
  public double getY() {
    return this.y;
  }

  /**
   * Sets the y coordinate
   *
   * @param y a y coordinate
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Changes y by a certain amount
   *
   * @param y number in pixels
   */
  public void changeY(double y) {
    this.setY(this.getY() + y);
  }

  public void setZoomLimit(double low, double high) {
    this.zoomLimitL = low;
    this.zoomLimitH = high;
  }

  public void setZoom(double zoom) {
    this.zoom = Math.max(Math.min(zoom, zoomLimitH), zoomLimitL);
  }

  public double getZoom() {
    return this.zoom;
  }

  public void resetZoom() {
    this.zoom = 100;
  }

  public void changeZoom(double dz) {
    this.setZoom(this.getZoom() + dz);
  }
}
