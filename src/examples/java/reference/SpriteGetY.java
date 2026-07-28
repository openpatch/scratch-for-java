package reference;
import org.openpatch.scratch.*;

public class SpriteGetY {
  public SpriteGetY() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    mySprite.say("Y: " + mySprite.getY());
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteGetY();
  }
}
