package reference;
import org.openpatch.scratch.*;

public class SpriteChangeTint {
  public SpriteChangeTint() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
