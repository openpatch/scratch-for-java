package reference;
import org.openpatch.scratch.*;


public class ShapeConstructors {
  public ShapeConstructors() {
    // The four shapes a hitbox can be built from. All of them are measured in
    // the sprite's own pixels, with 0, 0 in the middle of the costume.
    Shape circle = new Circle(0, 0, 20);
    Shape rectangle = new Rectangle(-20, -10, 40, 20);
    Shape ellipse = new Ellipse(-30, -10, 60, 20);
    Shape triangle = new Triangle(0, -20, 20, 20, -20, 20);
    System.out.println("the circle covers " + circle.getBounds().width() + " pixels across");
    System.out.println("the triangle covers " + triangle.getBounds().width() + " pixels across");
  }

  public static void main(String[] args) {
    new ShapeConstructors();
  }
}
