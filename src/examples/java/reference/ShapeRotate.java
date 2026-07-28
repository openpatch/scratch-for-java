package reference;
import org.openpatch.scratch.*;


public class ShapeRotate {
  public ShapeRotate() {
    Shape myShape = new Rectangle(-20, -10, 40, 20);
    // Turned a quarter of the way round its own middle, so what was 40 wide and
    // 20 high is now 20 wide and 40 high.
    Shape turned = myShape.rotate(90, 0, 0);
    System.out.println("before: " + myShape.getBounds().width() + " by " + myShape.getBounds().height());
    System.out.println("after: " + turned.getBounds().width() + " by " + turned.getBounds().height());
  }

  public static void main(String[] args) {
    new ShapeRotate();
  }
}
