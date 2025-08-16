package demos.tiled;

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
    var player = world.getPlayer();
    if (this.isTouchingSprite(player)) {
      this.getWindow().transitionToStage(new World(toMap, player), 500);
      player.setX(toX);
      player.setY(toY);
    }
  }
}
