package Sensing;

import java.awt.geom.Ellipse2D;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.hitbox.Hitbox;

public class Sensing extends Stage {
  public static Hero h, m;

  public Sensing() {
    super(800, 800);
    Window.getInstance().setDebug(true);
    h = new Hero();
    m = new MovableHero();
    this.add(h);
    this.add(m);
  }

  public void run() {
    this.display("Move the hero with WASD and rotate him with R");
  }

  public static void main(String[] args) {
    new Sensing();
  }
}

class Hero extends Sprite {
  public Hero() {
    super("hero", "Sensing/sprites/hero.png");
    this.addCostume("hero2", "Sensing/sprites/hero2.png");
    this.setSize(50);
    this.setDirection(45);
    this.move(80);

    this.setHitbox(0, 0, 300, 0, 300, 570, 0, 570, 150, 275);
  }

  public void run() {
    if (this.isTouchingMousePointer()) {
      this.switchCostume("hero2");
    } else {
      this.switchCostume("hero");
    }
  }
}

class MovableHero extends Hero {
  public MovableHero() {
    super();
    this.setPosition(-100, -100);
    this.setDirection(0);
    var hb = new Hitbox(new Ellipse2D.Double(0, 0, 615, 570));
    this.setHitbox(hb);
  }

  public void run() {
    super.run();
    if (this.isKeyPressed(KeyCode.VK_SPACE)) {
      this.move(1);
    }
    if (this.isKeyPressed(KeyCode.VK_A)) {
      this.changeX(-1);
    }
    if (this.isKeyPressed(KeyCode.VK_D)) {
      this.changeX(1);
    }
    if (this.isKeyPressed(KeyCode.VK_W)) {
      this.changeY(1);
    }
    if (this.isKeyPressed(KeyCode.VK_S)) {
      this.changeY(-1);
    }
    if (this.isKeyPressed(82)) {
      this.turnRight(1);
    }
    if (this.isTouchingSprite(Sensing.h)) {
      this.say("Hit");
    } else {
      this.say(null);
    }
  }
}
