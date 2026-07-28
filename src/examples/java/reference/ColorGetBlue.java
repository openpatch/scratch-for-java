package reference;
import org.openpatch.scratch.*;


public class ColorGetBlue {
  public ColorGetBlue() {
    Color myColor = new Color("#1e78dc");
    System.out.println("The blue part of #1e78dc is " + myColor.getBlue() + ".");
  }

  public static void main(String[] args) {
    new ColorGetBlue();
  }
}
