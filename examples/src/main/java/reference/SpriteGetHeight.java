package reference;
import org.openpatch.scratch.*;

public class SpriteGetHeight {
  public SpriteGetHeight() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.changeY(30);
    myStage.add(mySprite);
    mySprite.say("Height: " + mySprite.getHeight());
    myStage.waitSeconds(1);
  }

  public static void main(String[] args) {
    new SpriteGetHeight();
  }
}
