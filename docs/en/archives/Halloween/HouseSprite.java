import org.openpatch.scratch.*;

public class HouseSprite extends Sprite {
  public HouseSprite() {
    this.addCostume("on", "sprites/castle_on.png");
    this.addCostume("off", "sprites/castle_off.png");
    this.addSound("door", "sounds/door.wav");
    this.setSize(70);
    this.setY(240);
  }

  public void run() {
    if (this.isTouchingMousePointer()) {
      this.switchCostume("on");
    } else {
      this.switchCostume("off");
    }
    if (isTouchingMousePointer()) {
      this.playSound("door");
    }
  }
}
