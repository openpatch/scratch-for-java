import eu.barkmin.processing.scratch.*;

ScratchStage stage;
RobotSprite robot;

void setup() {
  size(800, 600);
  stage = ScratchStage.getInstance(this);
  robot = new RobotSprite();
  robot.setSize(20);
  robot.setRotation(65);
  robot.setPosition(width / 2, height / 2);
}

void draw() {
  stage.draw();
  robot.draw();
}
