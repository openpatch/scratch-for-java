package reference;
import org.openpatch.scratch.*;


public class ColorGetRed {
  public ColorGetRed() {
    Color myColor = new Color("#1e78dc");
    System.out.println("red " + myColor.getRed());
    System.out.println("green " + myColor.getGreen());
    System.out.println("blue " + myColor.getBlue());
  }

  public static void main(String[] args) {
    new ColorGetRed();
  }
}
