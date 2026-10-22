package reference;
import org.openpatch.scratch.*;

public class SpritePointTowardsMousePointer {
  public SpritePointTowardsMousePointer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    while (true) {
      mySprite.pointTowardsMousePointer();
    }
  }

  public static void main(String[] args) {
    new SpritePointTowardsMousePointer();
  }
}
