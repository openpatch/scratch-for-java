package org.openpatch.scratch;

public record Bounds(double x, double y, double width, double height) {
  public boolean intersects(Bounds other) {
    return this.x < other.x + other.width &&
        this.x + this.width > other.x &&
        this.y < other.y + other.height &&
        this.y + this.height > other.y;
  }
}
