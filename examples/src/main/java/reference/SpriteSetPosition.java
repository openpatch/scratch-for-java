package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteSetPosition {
  public SpriteSetPosition() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    while (true) {
      int x = myStage.pickRandom(-myStage.getWidth() / 2, myStage.getWidth() / 2);
      int y = myStage.pickRandom(-myStage.getHeight() / 2, myStage.getHeight() / 2);

      mySprite.setPosition(x, y);
      myStage.waitSeconds(0.2);
    }
  }

  public static void main(String[] args) {
    new SpriteSetPosition();
  }
}
