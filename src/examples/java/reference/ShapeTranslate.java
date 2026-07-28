package reference;
import org.openpatch.scratch.*;


public class ShapeTranslate {
  public ShapeTranslate() {
    Shape myShape = new Rectangle(-20, -10, 40, 20);
    // Every shape method hands back a new shape rather than changing this one.
    Shape moved = myShape.translate(100, 0);
    System.out.println("before: x = " + myShape.getBounds().x());
    System.out.println("after: x = " + moved.getBounds().x());
  }

  public static void main(String[] args) {
    new ShapeTranslate();
  }
}
