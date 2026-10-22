package reference;
import org.openpatch.scratch.*;

public class SpriteIfOnEdgeBounce {
  public SpriteIfOnEdgeBounce() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    mySprite.setRotationStyle(RotationStyle.LEFT_RIGHT);
    while (true) {
      mySprite.move(5);
      mySprite.ifOnEdgeBounce();
      myStage.waitSeconds(0.02);
    }
  }

  public static void main(String[] args) {
    new SpriteIfOnEdgeBounce();
  }
}
