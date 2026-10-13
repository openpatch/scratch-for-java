package reference;
import org.openpatch.scratch.*;


public class SpriteRemove {
  public SpriteRemove() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    myStage.wait(2000);
    // Takes the sprite off the stage. Its run() is not called again.
    mySprite.remove();
  }

  public static void main(String[] args) {
    new SpriteRemove();
  }
}
