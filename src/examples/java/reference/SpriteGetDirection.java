package reference;
import org.openpatch.scratch.*;

public class SpriteGetDirection {
  public SpriteGetDirection() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    mySprite.setDirection(45);
    mySprite.say("Direction: " + mySprite.getDirection());
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteGetDirection();
  }
}
