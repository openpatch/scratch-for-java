package reference;

import org.openpatch.scratch.*;

public class SpriteSetHitbox {
  public SpriteSetHitbox() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    myStage.wait(2000);
    // x and y of each corner, in turn
    mySprite.setHitbox(0, 0, 10, 0, 10, 10, 0, 10);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteSetHitbox();
  }
}
