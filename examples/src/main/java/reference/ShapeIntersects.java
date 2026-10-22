package reference;
import org.openpatch.scratch.*;


public class ShapeIntersects {
  public ShapeIntersects() {
    Shape left = new Circle(-30, 0, 50);
    Shape right = new Circle(30, 0, 50);
    Shape faraway = new Circle(300, 0, 50);
    // Whether two shapes overlap, which is what touching is worked out from.
    System.out.println("the two near ones: " + left.intersects(right));
    System.out.println("the far one: " + left.intersects(faraway));
  }

  public static void main(String[] args) {
    new ShapeIntersects();
  }
}
