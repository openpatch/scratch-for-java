import eu.barkmin.processing.scratch.*;

ScratchStage stage;
RobotSprite robot;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
  robot = new RobotSprite();
  stage = ScratchStage.getInstance();
  stage.addSprite(robot);
}

void draw() {}
