package reference;
import org.openpatch.scratch.*;

public class SpriteGetHeight {
  public SpriteGetHeight() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.changeY(30);
    myStage.add(mySprite);
    mySprite.say("Height: " + mySprite.getHeight());
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteGetHeight();
  }
}
