package reference;
import org.openpatch.scratch.*;


public class PolygonAddPoint {
  public PolygonAddPoint() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // The same shape built one corner at a time, which suits a shape worked out
    // while the program runs.
    Polygon myPolygon = new Polygon();
    myPolygon.addPoint(0, -25);
    myPolygon.addPoint(20, 0);
    myPolygon.addPoint(0, 25);
    myPolygon.addPoint(-20, 0);
    mySprite.setHitbox(myPolygon);
  }

  public static void main(String[] args) {
    new PolygonAddPoint();
  }
}
