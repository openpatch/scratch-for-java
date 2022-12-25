import org.openpatch.scratch.*;

Stage stage;
RandomDotSprite dot;

void setup() {
  size(800, 600);
  stage = new Stage(this);

  dot = new RandomDotSprite();
  stage.add(dot);
}

void draw() {}

class RandomDotSprite extends Sprite {
  void run() {
    if(this.getTimer().everyMillis(100)) {
      this.getPen().down();
      this.getPen().setSize(10);
      this.setPosition(Math.random() * Stage.parent.width, Math.random() * Stage.parent.height);
      this.getPen().changeColor(2);
      this.getPen().up();
    }
  }
}
