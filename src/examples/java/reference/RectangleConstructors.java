package reference;
import org.openpatch.scratch.*;


public class RectangleConstructors {
  public RectangleConstructors() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // An upright box, given by its top left corner and its size.
    mySprite.setHitbox(new Rectangle(-20, -20, 40, 40));
  }

  public static void main(String[] args) {
    new RectangleConstructors();
  }
}
