package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteSetY {
  public SpriteSetY() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    myStage.wait(2000);
    mySprite.setY(20);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteSetY();
  }
}
