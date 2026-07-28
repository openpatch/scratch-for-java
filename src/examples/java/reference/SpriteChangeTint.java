package reference;
import org.openpatch.scratch.*;

public class SpriteChangeTint {
  public SpriteChangeTint() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");

    myStage.add(mySprite);
    myStage.wait(2000);
    mySprite.changeTint(150);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteChangeTint();
  }
}
