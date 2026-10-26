package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteShow {
  public SpriteShow() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    myStage.waitSeconds(3);
    mySprite.hide();
    myStage.waitSeconds(3);
    mySprite.show();
    myStage.waitSeconds(3);
  }

  public static void main(String[] args) {
    new SpriteShow();
  }
}
