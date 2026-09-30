package demos.robot;

import org.openpatch.scratch.*;

public class RobotStage extends Stage {
  public RobotStage() {
    super(800, 600);
    this.setDebug(true);
    this.add(new RobotSprite());
  }

  public static void main(String[] args) {
    new RobotStage();
  }
}
