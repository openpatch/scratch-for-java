package reference;
import org.openpatch.scratch.*;


public class ColorConstructors {
  public ColorConstructors() {
    Stage myStage = new Stage(600, 240);

    // The four ways of naming a colour. The stage shows the last one.
    Color white = new Color();
    Color fromHex = new Color("#ff8800");
    Color fromHue = new Color(140);
    Color fromRgb = new Color(30, 120, 220);
    Color copy = new Color(fromRgb);
    System.out.println("white is " + white.getRed() + ", " + white.getGreen() + ", " + white.getBlue());
    System.out.println("#ff8800 has the hue " + fromHex.getH());
    System.out.println("hue 140 is " + fromHue.getRed() + ", " + fromHue.getGreen() + ", " + fromHue.getBlue());
    myStage.setColor(copy);
  }

  public static void main(String[] args) {
    new ColorConstructors();
  }
}
