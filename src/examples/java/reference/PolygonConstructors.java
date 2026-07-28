package reference;
import org.openpatch.scratch.*;


public class PolygonConstructors {
  public PolygonConstructors() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // A shape of as many corners as you like. This one is a rough diamond
    // around the middle of the costume.
    double[] x = { 0, 20, 0, -20 };
    double[] y = { -25, 0, 25, 0 };
    mySprite.setHitbox(new Polygon(x, y));
  }

  public static void main(String[] args) {
    new PolygonConstructors();
  }
}
