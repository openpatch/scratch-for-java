package reference;
import org.openpatch.scratch.*;

public class SpriteGetWidth {
  public SpriteGetWidth() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    mySprite.say("Width: " + mySprite.getWidth());
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteGetWidth();
  }
}
