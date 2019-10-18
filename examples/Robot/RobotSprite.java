import eu.barkmin.processing.scratch.*;

public class RobotSprite extends ScratchSprite {
  public RobotSprite() {
    this.addCostume("robot", "sprites/robot.png");
    this.setOnEdgeBounce(true);
    this.setSize(20);
    this.setRotation(65);

  }
  
  public void run() {
     this.move(2);
  }
}
