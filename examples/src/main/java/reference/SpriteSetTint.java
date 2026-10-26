package reference;
import org.openpatch.scratch.*;

public class SpriteSetTint {
  public SpriteSetTint() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    myStage.waitSeconds(2);
    // multiplies every pixel by the colour (white would leave the costume as it is)
    mySprite.setTint(new Color(160, 255, 160));
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new SpriteSetTint();
  }
}
