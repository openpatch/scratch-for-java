package reference;
import org.openpatch.scratch.*;


public class SpriteEnableHitbox {
  public SpriteEnableHitbox() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Window.getInstance().setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Touching is worked out from the hitbox, which debug mode draws.
    while (true) {
      mySprite.disableHitbox();
      myStage.waitSeconds(1.5);
      mySprite.enableHitbox();
      myStage.waitSeconds(1.5);
    }
  }

  public static void main(String[] args) {
    new SpriteEnableHitbox();
  }
}
