package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteSetX {
  public SpriteSetX() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    myStage.waitSeconds(2);
    mySprite.setX(50);
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new SpriteSetX();
  }
}
