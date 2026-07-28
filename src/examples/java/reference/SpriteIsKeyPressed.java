package reference;
import org.openpatch.scratch.*;

public class SpriteIsKeyPressed {
  public SpriteIsKeyPressed() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.changeX(-80);
    mySprite.changeY(30);
    myStage.add(mySprite);
    while (true) {
      mySprite.say("Space pressed? " + mySprite.isKeyPressed(KeyCode.SPACE));
    }
  }

  public static void main(String[] args) {
    new SpriteIsKeyPressed();
  }
}
