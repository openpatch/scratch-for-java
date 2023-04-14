import org.openpatch.scratch.*;

public class RobotSprite extends Sprite {

  public RobotSprite() {
    this.addCostume("robot", "sprites/robot.png");
    this.setSize(20);
    this.changeY(20);
  }

  public void run() {
    this.move(2);
    this.ifOnEdgeBounce();
  }
}
