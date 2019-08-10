import eu.barkmin.processing.scratch.*;

ScratchStage stage;
Hero h, m;

void setup() {
  size(800, 800);
  ScratchStage.init(this, true);
  stage = ScratchStage.getInstance();
  h = new Hero();
  m = new MovableHero();
}

void draw() {
  h.draw();
  m.draw();
}

class MovableHero extends Hero {
  void draw() {
    super.draw();
    if(isKeyPressed(65)) {
      this.turnLeft(1);
    }
    if(isKeyPressed(68)) {
      this.turnRight(1);
    }
    if(isKeyPressed(87)) {
      this.move(1);
    }
    if(isKeyPressed(83)) {
      this.move(-1);
    }
    if(isTouchingSprite(h)) {
      println("Ui a hero");
    } else {
      println("No hero :(");
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
