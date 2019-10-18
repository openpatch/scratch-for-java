import eu.barkmin.processing.scratch.*;

ScratchStage stage;
CatSprite myCat;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
  stage = ScratchStage.getInstance();
  myCat = new CatSprite();
  stage.addSprite(myCat);
}

void draw() {}

// Define a class Cat 
class CatSprite extends ScratchSprite {
  CatSprite() {
    this.addCostume("cat", "sprites/cat.png");  
    this.setOnEdgeBounce(true);
  }
  void run() {
    this.move(2);
  }
}
