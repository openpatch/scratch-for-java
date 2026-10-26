package reference;
import org.openpatch.scratch.*;


public class RectangleConstructors {
  public RectangleConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Window.getInstance().setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // An upright box, given by its top left corner and its size.
    mySprite.setHitbox(new Rectangle(-20, -20, 40, 40));
  }

  public static void main(String[] args) {
    new RectangleConstructors();
  }
}
