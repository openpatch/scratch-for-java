package reference;
import org.openpatch.scratch.*;


public class SpriteGoToMousePointer {
  public SpriteGoToMousePointer() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Click into the stage first, so that it takes the keyboard and the mouse.
    while (true) {
      mySprite.goToMousePointer();
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new SpriteGoToMousePointer();
  }
}
