package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetTint {
  public SpriteSetTint() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    myStage.wait(2000);
    mySprite.setTint(200);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteSetTint();
  }
}
