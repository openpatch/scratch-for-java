package reference;
import org.openpatch.scratch.*;


public class ColorConstructors {
  public ColorConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    // The ways of naming a colour. The stage shows the last one.
    Color white = new Color(255, 255, 255);
    Color fromHex = new Color("#ff8800");
    Color fromHue = Color.fromHSB(55, 100, 100);
    Color fromRgb = new Color(30, 120, 220);
    // Colours are immutable, so a colour can be shared instead of copied.
    Color copy = fromRgb;
    System.out.println("white is " + white.getRed() + ", " + white.getGreen() + ", " + white.getBlue());
    System.out.println("#ff8800 has the hue " + fromHex.getHue());
    System.out.println("hue 55 is " + fromHue.getRed() + ", " + fromHue.getGreen() + ", " + fromHue.getBlue());
    myStage.setColor(copy);
  }

  public static void main(String[] args) {
    new ColorConstructors();
  }
}
