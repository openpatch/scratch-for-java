package Tiled;

import org.openpatch.scratch.extensions.animation.*;

public class Enemy extends AnimatedSprite {

  protected String state = "idle";
  protected String dir = "down";
  protected double mapX = 0;
  protected double mapY = 0;

  public Enemy(double mapX, double mapY) {
    this.mapX = mapX;
    this.mapY = mapY;
  }

  public void setMapX(double mapX) {
    this.mapX = mapX;
    this.setX(this.mapX - GameState.get().camX);
  }

  public void changeMapX(double dx) {
    this.setMapX(this.mapX + dx);
  }

  public void setMapY(double mapY) {
    this.mapY = mapY;
    this.setY(this.mapY - GameState.get().camY);
  }

  public void changeMapY(double dy) {
    this.setMapY(this.mapY + dy);
  }
}
