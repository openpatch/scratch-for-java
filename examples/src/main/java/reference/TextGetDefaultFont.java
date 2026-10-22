package reference;
import org.openpatch.scratch.*;


public class TextGetDefaultFont {
  public TextGetDefaultFont() {
    // The font every text starts out with.
    System.out.println("The default font is " + Text.getDefaultFont() + ".");
  }

  public static void main(String[] args) {
    new TextGetDefaultFont();
  }
}
