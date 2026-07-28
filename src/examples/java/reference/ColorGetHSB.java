package reference;
import org.openpatch.scratch.*;


public class ColorGetHSB {
  public ColorGetHSB() {
    Color myColor = new Color("#1e78dc");
    // getHSB() is the hue on its own - the same value getH() returns.
    System.out.println("The hue of #1e78dc is " + myColor.getHSB() + ".");
  }

  public static void main(String[] args) {
    new ColorGetHSB();
  }
}
