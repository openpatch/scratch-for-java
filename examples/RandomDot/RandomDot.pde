import eu.barkmin.processing.scratch.*;

RandomDotSprite dot;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
  dot = new RandomDotSprite();
  
}

void draw() {
  dot.draw();
}

class RandomDotSprite extends ScratchSprite {
  void draw() {
    super.draw();
    if(this.getTimer().everyMillis(100)) {
      this.getPen().down();
      this.getPen().setSize(10);
      this.setPosition(Math.random() * ScratchStage.parent.width, Math.random() * ScratchStage.parent.height);
      this.getPen().changeColor(2);
      this.getPen().up();
    }
  }
}
