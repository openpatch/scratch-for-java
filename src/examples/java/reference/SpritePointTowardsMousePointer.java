package reference;
import org.openpatch.scratch.*;

public class SpritePointTowardsMousePointer {
  public SpritePointTowardsMousePointer() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    while (true) {
      mySprite.pointTowardsMousePointer();
    }
  }

  public static void main(String[] args) {
    new SpritePointTowardsMousePointer();
  }
}
