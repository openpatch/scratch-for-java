package reference;
import org.openpatch.scratch.*;


public class SpriteClone {
  public SpriteClone() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // A copy that is added to the same stage, next to the original.
    for (int i = 0; i < 8; i++) {
      Sprite copy = mySprite.createClone();
      copy.goToRandomPosition();
      myStage.waitSeconds(0.5);
    }
  }

  public static void main(String[] args) {
    new SpriteClone();
  }
}
