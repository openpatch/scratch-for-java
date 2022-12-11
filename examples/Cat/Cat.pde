import org.openpatch.scratch.*;

Stage stage;
CatSprite myCat;

void setup() {
  size(800, 600);
  stage = new Stage(this);
  myCat = new CatSprite();
  stage.add(myCat);
}

void draw() {}

// Define a class Cat 
class CatSprite extends Sprite {
  CatSprite() {
    this.addCostume("cat", "sprites/cat.png");  
    this.setOnEdgeBounce(true);
  }
  void run() {
    this.move(2);
  }
}
