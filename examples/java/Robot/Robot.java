package Robot;

import org.openpatch.scratch.*;

public class Robot extends Stage {
  public Robot() {
    super(800, 600);
    this.setDebug(true);
    this.add(new RobotSprite());
  }

  public static void main(String[] args) {
    new Robot();
  }
}
