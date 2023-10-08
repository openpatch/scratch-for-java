package Tiled;

import org.openpatch.scratch.extensions.animation.*;

public class Fireball extends AnimatedSprite {
  private double mapX;
  private double mapY;
  private String dir;
  private double speed = 3;

  public Fireball(double mapX, double mapY, String dir) {
    this.mapX = mapX;
    this.mapY = mapY;
    this.dir = dir;

    this.addAnimation("default", "Tiled/assets/Fireball.png", 4, 32, 32);
  }

  public void run() {
    if (this.getTimer().afterMillis(1000)) {
      this.remove();
    }

    if (dir == "left") {
      this.mapX -= speed;
    } else if (dir == "right") {
      this.mapX += speed;
    } else if (dir == "up") {
      this.mapY += speed;
    } else if (dir == "down") {
      this.mapY -= speed;
    }

    this.setX(this.mapX - GameState.get().camX);
    this.setY(this.mapY - GameState.get().camY);
    this.playAnimation("default");
  }
}
