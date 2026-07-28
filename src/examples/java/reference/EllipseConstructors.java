package reference;
import org.openpatch.scratch.*;


public class EllipseConstructors {
  public EllipseConstructors() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // A flattened circle, given by the box it fits into.
    mySprite.setHitbox(new Ellipse(-25, -15, 50, 30));
  }

  public static void main(String[] args) {
    new EllipseConstructors();
  }
}
