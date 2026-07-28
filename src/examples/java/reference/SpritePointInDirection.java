package reference;
import org.openpatch.scratch.*;


public class SpritePointInDirection {
  public SpritePointInDirection() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // 0 is up, 90 is right, 180 is down, 270 is left.
    while (true) {
      mySprite.pointInDirection(90);
      myStage.wait(700);
      mySprite.pointInDirection(180);
      myStage.wait(700);
      // A vector works too: the direction it points in.
      mySprite.pointInDirection(new Vector2(-1, 0));
      myStage.wait(700);
    }
  }

  public static void main(String[] args) {
    new SpritePointInDirection();
  }
}
