package demos.robot;

import org.openpatch.scratch.*;

public class RobotStage extends Stage {

  // begin stage designer
  private RobotSprite robotSprite;
  // end stage designer

  public RobotStage() {
    Window.getInstance().setDebug(true);
    // begin stage designer
    robotSprite = new RobotSprite();
    this.add(robotSprite);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(800, 600);
    myWindow.setStage(new RobotStage());
  }
}
