package demos.robot;

import org.openpatch.scratch.Sprite;

public class RobotSprite extends Sprite {

  public RobotSprite() {
    this.addCostume("robot", "demos/robot/sprites/robot.png");
    this.setSize(20);
    this.changeY(20);
    this.setDirection(45);
  }

  public void run() {
    this.move(2);
    this.ifOnEdgeBounce();
  }
}
