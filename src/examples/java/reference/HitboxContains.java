package reference;
import org.openpatch.scratch.*;


public class HitboxContains {
  public HitboxContains() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    Hitbox myHitbox = mySprite.getHitbox();
    // Whether a point is inside the sprite - the same question isTouchingMousePointer()
    // asks, but about any point at all.
    while (true) {
      myStage.display("mouse inside: " + myHitbox.contains(myStage.getMouseX(), myStage.getMouseY()));
      myStage.wait(50);
    }
  }

  public static void main(String[] args) {
    new HitboxContains();
  }
}
