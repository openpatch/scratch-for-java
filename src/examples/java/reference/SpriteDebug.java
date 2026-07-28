package reference;
import org.openpatch.scratch.*;


public class SpriteDebug {
  public SpriteDebug() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    myStage.setDebug(true);
    // Values printed onto the stage next to the sprite, without stopping it.
    while (true) {
      mySprite.changeX(2);
      mySprite.debug("x", mySprite.getX(), "direction", mySprite.getDirection());
      myStage.wait(50);
    }
  }

  public static void main(String[] args) {
    new SpriteDebug();
  }
}
