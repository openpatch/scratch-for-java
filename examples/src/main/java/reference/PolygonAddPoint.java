package reference;
import org.openpatch.scratch.*;


public class PolygonAddPoint {
  public PolygonAddPoint() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Window.getInstance().setDebug(true);
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
