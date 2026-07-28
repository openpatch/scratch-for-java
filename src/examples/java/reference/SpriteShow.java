package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteShow {
  public SpriteShow() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    myStage.wait(3000);
    mySprite.hide();
    myStage.wait(3000);
    mySprite.show();
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteShow();
  }
}
