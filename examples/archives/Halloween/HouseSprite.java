import org.openpatch.scratch.Sprite;

public class HouseSprite extends Sprite {
  public HouseSprite() {
    this.addCostume("on", "sprites/castle_on.png");
    this.addCostume("off", "sprites/castle_off.png");
    this.addSound("door", "sounds/door.wav");
    this.setSize(70);
    this.setY(0);
    this.turnRight(20);
  }

  public void run() {
    if (this.isTouchingMousePointer()) {
      this.playSound("door");
      this.switchCostume("on");
    } else {
      this.switchCostume("off");
    }
  }
}
