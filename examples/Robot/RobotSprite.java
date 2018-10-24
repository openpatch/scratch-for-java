import eu.barkmin.processing.scratch.*;

public class RobotSprite extends ScratchSprite {
  public RobotSprite() {
    super("robot", "sprites/robot.png");
    this.setOnEdgeBounce(true);
    this.setSize(20);
    this.setRotation(65);

  }
  
  public void draw() {
     super.draw();
     this.move(2);
  }
}
