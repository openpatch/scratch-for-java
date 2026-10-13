package reference;
import org.openpatch.scratch.*;

public class SpriteChangeX {
  public SpriteChangeX() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    myStage.wait(2000);
    mySprite.changeX(100);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteChangeX();
  }
}
