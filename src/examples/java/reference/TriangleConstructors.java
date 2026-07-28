package reference;
import org.openpatch.scratch.*;


public class TriangleConstructors {
  public TriangleConstructors() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Three corners, which suits an arrow or a spike.
    mySprite.setHitbox(new Triangle(0, -25, 22, 20, -22, 20));
  }

  public static void main(String[] args) {
    new TriangleConstructors();
  }
}
