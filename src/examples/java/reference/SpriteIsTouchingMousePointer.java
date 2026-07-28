package reference;
import org.openpatch.scratch.*;

public class SpriteIsTouchingMousePointer {
  public SpriteIsTouchingMousePointer() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.changeX(-100);
    mySprite.changeY(30);
    myStage.add(mySprite);
    while (true) {
      mySprite.say("Is touching mouse? " + mySprite.isTouchingMousePointer());
    }
  }

  public static void main(String[] args) {
    new SpriteIsTouchingMousePointer();
  }
}
