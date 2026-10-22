package reference;
import org.openpatch.scratch.*;


public class ShapeContains {
  public ShapeContains() {
    Shape myShape = new Circle(0, 0, 50);
    // Whether a point lies inside the shape.
    System.out.println("the middle: " + myShape.contains(0, 0));
    System.out.println("40 pixels out: " + myShape.contains(40, 0));
    System.out.println("60 pixels out: " + myShape.contains(60, 0));
  }

  public static void main(String[] args) {
    new ShapeContains();
  }
}
