package reference;
import org.openpatch.scratch.*;


public class ShapeScale {
  public ShapeScale() {
    Shape myShape = new Rectangle(-20, -10, 40, 20);
    Shape twiceAsWide = myShape.scale(2, 1);
    System.out.println("before: " + myShape.getBounds().width() + " wide");
    System.out.println("after: " + twiceAsWide.getBounds().width() + " wide");
  }

  public static void main(String[] args) {
    new ShapeScale();
  }
}
