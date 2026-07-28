package reference;
import org.openpatch.scratch.*;


public class ColorGet {
  public ColorGet() {
    Color myColor = new Color(30, 120, 220);
    // The colour packed into a single number, the way java.awt.Color stores it.
    System.out.println("As one number this colour is " + myColor.get() + ".");
  }

  public static void main(String[] args) {
    new ColorGet();
  }
}
