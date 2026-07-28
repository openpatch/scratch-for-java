package reference;
import org.openpatch.scratch.*;


public class CircleConstructors {
  public CircleConstructors() {
    Stage myStage = new Stage(600, 240);
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
