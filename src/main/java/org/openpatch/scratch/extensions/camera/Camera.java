package org.openpatch.scratch.extensions.camera;

import org.openpatch.scratch.Vector2;

/**
 * The Camera class represents a camera that can be used to view a scene. It provides methods to set
 * the position of the camera, convert between screen and local coordinates, and zoom in and out.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * Camera camera = new Camera();
 * camera.setPosition(0, 0);
 * camera.setZoom(200);
 * }</pre>
 *
 * @example.files CameraConstructors.java
 */
public class Camera {
  private double x;
  private double y;
  private double zoom;
  private double zoomLimitH;
  private double zoomLimitL;

  /**
   * Constructs a new Camera with default values. The default position is (0, 0) and the default
   * zoom level is 100.
   */
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
   *
   * @example.files CameraSetPosition.java
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

  /**
   * Returns the position of the sprite
   *
   * @return a vector
   *
   * @example.files CameraGetPosition.java
   */
  public Vector2 getPosition() {
    return new Vector2(x, y);
  }

  /**
   * Converts screen coordinates to local camera coordinates
   *
   * @param v screen coordinates
   * @return local coordinates
   *
   * @example.files CameraToLocalPosition.java
   */
  public Vector2 toLocalPosition(Vector2 v) {
    return new Vector2(
        v.getX() / (this.getZoom() / 100.0) + this.getX(),
        v.getY() / (this.getZoom() / 100.0) + this.getY());
  }

  /**
   * Converts screen x coordinate to local camera x coordinate
   *
   * @param x screen x coordinate
   * @return local x coordinate
   *
   * @example.files CameraToLocalX.java
   */
  public double toLocalX(double x) {
    return toLocalPosition(new Vector2(x, 0)).getX();
  }

  /**
   * Converts screen y coordinate to local camera y coordinate
   *
   * @param y screen y coordinate
   * @return local y coordinate
   *
   * @example.files CameraToLocalY.java
   */
  public double toLocalY(double y) {
    return toLocalPosition(new Vector2(0, y)).getY();
  }

  /**
   * Converts local camera coordinates to screen coordinates
   *
   * @param v local camera coordinates
   * @return screen coordinates
   *
   * @example.files CameraToGlobalPosition.java
   */
  public Vector2 toGlobalPosition(Vector2 v) {
    return new Vector2(
        (v.getX() - this.getX()) * (this.getZoom() / 100.0),
        (v.getY() - this.getY()) * (this.getZoom() / 100.0));
  }

  /**
   * Converts local camera x coordinate to screen x coordinate
   *
   * @param x local camera x coordinate
   * @return screen x coordinate
   *
   * @example.files CameraToGlobalX.java
   */
  public double toGlobalX(double x) {
    return this.toGlobalPosition(new Vector2(x, 0)).getX();
  }

  /**
   * Converts local camera y coordinate to screen y coordinate
   *
   * @param y local camera y coordinate
   * @return screen y coordinate
   *
   * @example.files CameraToGlobalY.java
   */
  public double toGlobalY(double y) {
    return this.toGlobalPosition(new Vector2(0, y)).getY();
  }

  /**
   * Returns the x coordinate of the sprite
   *
   * @return a x coordinate
   *
   * @example.files CameraGetX.java
   */
  public double getX() {
    return this.x;
  }

  /**
   * Sets the x coordinate
   *
   * @param x a x coordinate
   *
   * @example.files CameraSetX.java
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Changes x by a certain amount
   *
   * @param x number in pixels
   *
   * @example.files CameraChangeX.java
   */
  public void changeX(double x) {
    this.setX(this.getX() + y);
  }

  /**
   * Returns the y coordinate of the sprite
   *
   * @return a y coordinate
   *
   * @example.files CameraGetY.java
   */
  public double getY() {
    return this.y;
  }

  /**
   * Sets the y coordinate
   *
   * @param y a y coordinate
   *
   * @example.files CameraSetY.java
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Changes y by a certain amount
   *
   * @param y number in pixels
   *
   * @example.files CameraChangeY.java
   */
  public void changeY(double y) {
    this.setY(this.getY() + y);
  }

  /**
   * Sets the zoom limits
   *
   * @param low the lower limit
   * @param high the upper limit
   *
   * @example.files CameraSetZoomLimit.java
   */
  public void setZoomLimit(double low, double high) {
    this.zoomLimitL = low;
    this.zoomLimitH = high;
  }

  /**
   * Sets the zoom
   *
   * @param zoom the zoom level
   *
   * @example.files CameraSetZoom.java
   */
  public void setZoom(double zoom) {
    this.zoom = Math.max(Math.min(zoom, zoomLimitH), zoomLimitL);
  }

  /**
   * Returns the zoom level
   *
   * @return the zoom level
   *
   * @example.files CameraGetZoom.java
   */
  public double getZoom() {
    return this.zoom;
  }

  /**
   * Resets the zoom level to 100
   *
   * @example.files CameraResetZoom.java
   */
  public void resetZoom() {
    this.zoom = 100;
  }

  /**
   * Changes the zoom level by a certain amount
   *
   * @param dz the amount to change the zoom level by
   *
   * @example.files CameraChangeZoom.java
   */
  public void changeZoom(double dz) {
    this.setZoom(this.getZoom() + dz);
  }
}
