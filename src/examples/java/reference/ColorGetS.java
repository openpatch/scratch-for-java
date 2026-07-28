package reference;
import org.openpatch.scratch.*;


public class ColorGetS {
  public ColorGetS() {
    Color strong = new Color("#ff0000");
    Color pale = new Color("#ffaaaa");
    System.out.println("A strong red has the saturation " + strong.getS() + ".");
    System.out.println("A pale red has the saturation " + pale.getS() + ".");
  }

  public static void main(String[] args) {
    new ColorGetS();
  }
}
