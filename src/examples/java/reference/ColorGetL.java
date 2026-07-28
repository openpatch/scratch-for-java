package reference;
import org.openpatch.scratch.*;


public class ColorGetL {
  public ColorGetL() {
    Color bright = new Color("#ff0000");
    Color dark = new Color("#550000");
    System.out.println("A bright red has the brightness " + bright.getL() + ".");
    System.out.println("A dark red has the brightness " + dark.getL() + ".");
  }

  public static void main(String[] args) {
    new ColorGetL();
  }
}
