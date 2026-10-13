package reference;
import org.openpatch.scratch.*;


public class SpriteEnableHitbox {
  public SpriteEnableHitbox() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
