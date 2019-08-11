import eu.barkmin.processing.scratch.*;

ScratchStage stage;
Hero h, m;

boolean hit = false;

void setup() {
  size(800, 800, P2D);
  ScratchStage.init(this, true);
  stage = ScratchStage.getInstance();
  h = new Hero();
  m = new MovableHero();
}

void draw() {
  textAlign(CENTER);
  
  fill(0);
  text("Move the hero with WASD and rotate him with R", width / 2, 40);
  text("Hit: " + hit, width / 2, 80);
  h.draw();
  m.draw();
}

class MovableHero extends Hero {
  MovableHero() {
    super();
    this.setPosition(332, 578);
    this.setRotation(270);
  }
  void draw() {
    super.draw();
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
      hit = true;
    } else {
      hit = false;
    }
  }
}

class Hero extends ScratchSprite {
  Hero() {
    super("hero", "sprites/hero.png");
    this.addCostume("hero2", "sprites/hero2.png");
    this.setSize(50);
    this.setRotation(45);
    this.move(280);
    
    int[] xHitbox = {0, 300, 300, 0, 150};
    int[] yHitbox = {0, 0,   570, 570, 275};
    this.setHitbox(xHitbox, yHitbox);
  }
  
  void draw() {
    super.draw();
    if(isTouchingMousePointer()) {
      this.switchCostume("hero2");
    } else {
      this.switchCostume("hero");
    }
  }
}
