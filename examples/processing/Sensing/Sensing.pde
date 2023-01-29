import org.openpatch.scratch.*;

Stage stage;
Hero h, m;

boolean hit = false;

void setup() {
  size(800, 800, P2D);
  stage = new Stage(this, true);
  h = new Hero();
  m = new MovableHero();
  stage.display("Move the hero with WASD and rotate him with R");
}

void draw() {
}

class MovableHero extends Hero {
  MovableHero() {
    super();
    this.setPosition(332, 578);
    this.setDirection(270);
  }
  void run() {
    if(isKeyPressed(65)) {
      this.changeX(-1);;
    }
    if(isKeyPressed(68)) {
      this.changeX(1);
    }
    if(isKeyPressed(87)) {
      this.changeY(-1);
    }
    if(isKeyPressed(83)) {
      this.changeY(1);
    }
    if(isKeyPressed(82)) {
      this.turnRight(1);
    }
    if(isTouchingSprite(h)) {
      this.say("Hit");
    } else {
      this.say(null);
    }
  }
}

class Hero extends Sprite {
  Hero() {
    super("hero", "sprites/hero.png");
    this.addCostume("hero2", "sprites/hero2.png");
    this.setSize(50);
    this.setDirection(45);
    this.move(280);
    
    int[] xHitbox = {0, 300, 300, 0, 150};
    int[] yHitbox = {0, 0,   570, 570, 275};
    this.setHitbox(xHitbox, yHitbox);
  }
  
  void run() {
    if(isTouchingMousePointer()) {
      this.switchCostume("hero2");
    } else {
      this.switchCostume("hero");
    }
  }
}
