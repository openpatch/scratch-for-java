package reference;
import org.openpatch.scratch.*;


public class TriangleConstructors {
  public TriangleConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Window.getInstance().setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Three corners, which suits an arrow or a spike.
    mySprite.setHitbox(new Triangle(0, -25, 22, 20, -22, 20));
  }

  public static void main(String[] args) {
    new TriangleConstructors();
  }
}
