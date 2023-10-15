package Tiled;

import org.openpatch.scratch.*;

public class Warp extends Sprite {
  private String toMap;
  private double toX;
  private double toY;
  private World world;

  public Warp(World world, double x, double y, String toMap, double toX, double toY) {
    this.world = world;
    this.setX(x);
    this.setY(y);
    this.toMap = toMap;
    this.toX = toX;
    this.toY = toY;
  }

  public void run() {
    if (this.isTouchingSprite(World.PLAYER)) {
      this.world.loadMap(toMap);
      World.PLAYER.setX(toX);
      World.PLAYER.setY(toY);
    }
  }
}
