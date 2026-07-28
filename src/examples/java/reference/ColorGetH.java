package reference;
import org.openpatch.scratch.*;


public class ColorGetH {
  public ColorGetH() {
    Color myColor = new Color("#1e78dc");
    System.out.println("hue " + myColor.getH());
    System.out.println("saturation " + myColor.getS());
    System.out.println("brightness " + myColor.getL());
  }

  public static void main(String[] args) {
    new ColorGetH();
  }
}
