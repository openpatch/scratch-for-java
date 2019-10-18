import eu.barkmin.processing.scratch.*;

ScratchStage stage;
RandomDotSprite dot;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
  stage = ScratchStage.getInstance();
  dot = new RandomDotSprite();
  stage.addSprite(dot);
}

void draw() {}

class RandomDotSprite extends ScratchSprite {
  void run() {
    if(this.getTimer().everyMillis(100)) {
      this.getPen().down();
      this.getPen().setSize(10);
      this.setPosition(Math.random() * ScratchStage.parent.width, Math.random() * ScratchStage.parent.height);
      this.getPen().changeColor(2);
      this.getPen().up();
    }
  }
}
