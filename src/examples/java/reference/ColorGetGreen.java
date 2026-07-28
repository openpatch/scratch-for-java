package reference;
import org.openpatch.scratch.*;


public class ColorGetGreen {
  public ColorGetGreen() {
    Color myColor = new Color("#1e78dc");
    System.out.println("The green part of #1e78dc is " + myColor.getGreen() + ".");
  }

  public static void main(String[] args) {
    new ColorGetGreen();
  }
}
