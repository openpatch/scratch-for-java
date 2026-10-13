package reference;
import org.openpatch.scratch.*;


public class CircleConstructors {
  public CircleConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // A round hitbox, which suits anything that rolls.
    mySprite.setHitbox(new Circle(0, 0, 22));
  }

  public static void main(String[] args) {
    new CircleConstructors();
  }
}
