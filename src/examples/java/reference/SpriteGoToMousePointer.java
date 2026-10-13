package reference;
import org.openpatch.scratch.*;


public class SpriteGoToMousePointer {
  public SpriteGoToMousePointer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
