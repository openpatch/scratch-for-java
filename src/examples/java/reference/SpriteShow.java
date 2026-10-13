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
