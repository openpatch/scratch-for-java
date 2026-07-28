package reference;
import org.openpatch.scratch.*;


public class SpriteDisableHitbox {
  public SpriteDisableHitbox() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Without a hitbox the sprite touches nothing, which is what a background
    // decoration wants.
    mySprite.disableHitbox();
  }

  public static void main(String[] args) {
    new SpriteDisableHitbox();
  }
}
