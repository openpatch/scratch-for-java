import eu.barkmin.processing.scratch.*;

ScratchStage stage;
ClockSprite clock;

void setup() {
  size(800, 800);
  ScratchStage.init(this);
  stage = ScratchStage.getInstance();
  clock = new ClockSprite();
  stage.addSprite(clock);
}

void draw() {
}
