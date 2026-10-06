package demos.robot;

import org.openpatch.scratch.*;

public class RobotStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private RobotSprite robotSprite;
  // scratch4j:end fields

  public RobotStage() {
    super(800, 600);
    this.setDebug(true);
    // scratch4j:begin setup (managed by the stage designer)
    robotSprite = new RobotSprite();
    this.add(robotSprite);
    // scratch4j:end setup
  }

  public static void main(String[] args) {
    new RobotStage();
  }
}
