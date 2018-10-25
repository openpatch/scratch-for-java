import eu.barkmin.processing.scratch.*;

ScratchStage stage;
DotSprite dot;

void setup() {
  size(400, 200);
  frameRate(60);
  ScratchStage.init(this);
  stage = ScratchStage.getInstance();
  dot = new DotSprite();
}

void draw() {
  dot.draw();
  if (stage.getTimer().everyMillis(2400)) {
    stage.eraseAll();
  }
}

class DotSprite extends ScratchSprite {  
  DotSprite() {
    super();
    this.addTimer("timer2");
    this.addTimer("timer1");
    this.getPen().setSize(20);
    this.setOnEdgeBounce(true);
    this.setRotation(65);
  }
  
  void draw() {
    super.draw();
    if(this.getTimer("timer2").everyMillis(600)) {
      this.getPen().down();
      this.getPen().setColor(200);
      this.move(20);
      this.getPen().up();
    }
    if(this.getTimer("timer1").everyMillis(1200)) {
      this.getPen().down();
      this.getPen().setColor(100);
      this.move(20);
      this.getPen().up();
    }
  }
}
