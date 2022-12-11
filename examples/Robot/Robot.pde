import org.openpatch.scratch.*;

Stage stage;
RobotSprite robot;

void setup() {
  size(800, 600);
  stage = new Stage(this);
  robot = new RobotSprite();

  stage.add(robot);
}

void draw() {}
