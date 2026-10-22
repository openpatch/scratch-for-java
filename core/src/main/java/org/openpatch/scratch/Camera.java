package org.openpatch.scratch;

/**
 * The camera of a stage: which part of the world the stage shows. Every layer but
 * {@link Layer#UI} follows it, the backdrop included; pens draw where the camera shows their
 * position when they draw. Unused, it shows the world as it is: position (0, 0), zoom 100.
 *
 * <pre>{@code
 * Camera camera = this.getStage().getCamera();
 * camera.setPosition(player.getX(), player.getY());
 * }</pre>
 *
 * <p>World coordinates are where sprites are; screen coordinates are where something shows on
 * the stage, with (0, 0) in its centre. The mouse is reported in world coordinates by
 * {@code getMouseX()}, so it can be compared with sprite positions directly.
 */
public final class Camera {

  private double x;
  private double y;
  private double zoom = 100;
  private double zoomMin = 50;
  private double zoomMax = 200;

  /** A camera at (0, 0) with zoom 100. Every stage has its own: {@code getStage().getCamera()}. */
  public Camera() {}

  /**
   * Centres the camera on a world position.
   *
   * @param x world x
   * @param y world y
   *
   * @example.files CameraSetPosition.java
   */
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Centres the camera on a world position.
   *
   * @param position world position
   *
   * @example.files CameraSetPosition.java
   */
  public void setPosition(Vector2 position) {
    this.setPosition(position.getX(), position.getY());
  }

  /**
   * @return the world position at the stage's centre
   *
   * @example.files CameraGetPosition.java
   */
  public Vector2 getPosition() {
    return new Vector2(this.x, this.y);
  }

  /**
   * @return the world x at the stage's centre
   *
   * @example.files CameraGetX.java
   */
  public double getX() {
    return this.x;
  }

  /**
   * @param x the world x to show at the stage's centre
   *
   * @example.files CameraSetX.java
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * @param dx how far to move the camera along x
   *
   * @example.files CameraChangeX.java
   */
  public void changeX(double dx) {
    this.x += dx;
  }

  /**
   * @return the world y at the stage's centre
   *
   * @example.files CameraGetY.java
   */
  public double getY() {
    return this.y;
  }

  /**
   * @param y the world y to show at the stage's centre
   *
   * @example.files CameraSetY.java
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * @param dy how far to move the camera along y
   *
   * @example.files CameraChangeY.java
   */
  public void changeY(double dy) {
    this.y += dy;
  }

  /**
   * Sets the zoom in percent: 200 shows everything twice as large, 50 half as large. It stays
   * within the zoom limits (50 to 200 unless changed).
   *
   * @param zoom the zoom in percent
   *
   * @example.files CameraSetZoom.java
   */
  public void setZoom(double zoom) {
    this.zoom = Math.max(this.zoomMin, Math.min(this.zoomMax, zoom));
  }

  /**
   * @param dz percentage points to add to the zoom
   *
   * @example.files CameraChangeZoom.java
   */
  public void changeZoom(double dz) {
    this.setZoom(this.zoom + dz);
  }

  /**
   * @return the zoom in percent
   *
   * @example.files CameraGetZoom.java
   */
  public double getZoom() {
    return this.zoom;
  }

  /**
   * Back to zoom 100.
   *
   * @example.files CameraResetZoom.java
   */
  public void resetZoom() {
    this.zoom = 100;
  }

  /**
   * Limits the zoom.
   *
   * @param min the smallest zoom in percent
   * @param max the largest zoom in percent
   *
   * @example.files CameraSetZoomLimit.java
   */
  public void setZoomLimit(double min, double max) {
    this.zoomMin = min;
    this.zoomMax = max;
    this.setZoom(this.zoom);
  }

  /**
   * Where a world position shows on the stage.
   *
   * @param world a world position
   * @return the screen position (origin at the stage's centre)
   */
  public Vector2 toScreenPosition(Vector2 world) {
    return new Vector2(this.toScreenX(world.getX()), this.toScreenY(world.getY()));
  }

  /**
   * @param worldX a world x
   * @return where it shows on the stage
   */
  public double toScreenX(double worldX) {
    return (worldX - this.x) * this.zoom / 100;
  }

  /**
   * @param worldY a world y
   * @return where it shows on the stage
   */
  public double toScreenY(double worldY) {
    return (worldY - this.y) * this.zoom / 100;
  }

  /**
   * Which world position shows at a place on the stage.
   *
   * @param screen a screen position (origin at the stage's centre)
   * @return the world position
   */
  public Vector2 toWorldPosition(Vector2 screen) {
    return new Vector2(this.toWorldX(screen.getX()), this.toWorldY(screen.getY()));
  }

  /**
   * @param screenX an x on the stage
   * @return the world x shown there
   */
  public double toWorldX(double screenX) {
    return screenX * 100 / this.zoom + this.x;
  }

  /**
   * @param screenY a y on the stage
   * @return the world y shown there
   */
  public double toWorldY(double screenY) {
    return screenY * 100 / this.zoom + this.y;
  }
}
