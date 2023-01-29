import org.openpatch.scratch.*;

public class RobotSprite extends Sprite {
  public RobotSprite() {
    this.addCostume("robot", "sprites/robot.png");
    this.setOnEdgeBounce(true);
    this.setSize(20);
    this.setDirection(65);

  }
  
  public void run() {
     this.move(2);
  }
}