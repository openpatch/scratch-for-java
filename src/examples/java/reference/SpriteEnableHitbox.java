package reference;
import org.openpatch.scratch.*;


public class SpriteEnableHitbox {
  public SpriteEnableHitbox() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Touching is worked out from the hitbox, which debug mode draws.
    while (true) {
      mySprite.disableHitbox();
      myStage.wait(1500);
      mySprite.enableHitbox();
      myStage.wait(1500);
    }
  }

  public static void main(String[] args) {
    new SpriteEnableHitbox();
  }
}
