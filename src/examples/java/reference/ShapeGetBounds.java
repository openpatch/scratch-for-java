package reference;
import org.openpatch.scratch.*;


public class ShapeGetBounds {
  public ShapeGetBounds() {
    Shape myShape = new Triangle(0, -20, 20, 20, -20, 20);
    Bounds myBounds = myShape.getBounds();
    // The smallest upright rectangle the shape fits into.
    System.out.println("x " + myBounds.x() + ", y " + myBounds.y());
    System.out.println("width " + myBounds.width() + ", height " + myBounds.height());
  }

  public static void main(String[] args) {
    new ShapeGetBounds();
  }
}
