import eu.barkmin.processing.scratch.*;

RobotSprite robot;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
  robot = new RobotSprite();
}

void draw() {
  robot.draw();
}
