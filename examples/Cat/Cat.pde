import eu.barkmin.processing.scratch.*;

CatSprite myCat;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
  myCat = new CatSprite();
}

void draw() {
  myCat.draw();
}

// Define a class Cat 
class CatSprite extends ScratchSprite {
  CatSprite() {
    super("cat", "sprites/cat.png");  
    this.setOnEdgeBounce(true);
  }
  void draw() {
    super.draw();
    this.move(2);
  }
}
