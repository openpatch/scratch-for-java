package Tiled;

import org.openpatch.scratch.*;

public class Wall extends Sprite {
  private double mapX;
  private double mapY;

  public Wall(double mapX, double mapY) {
    this.mapX = mapX;
    this.mapY = mapY;
  }

  public void run() {
    this.setX(this.mapX - GameState.get().camX);
    this.setY(this.mapY - GameState.get().camY);
  }
}
