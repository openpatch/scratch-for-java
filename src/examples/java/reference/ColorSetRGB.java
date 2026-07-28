package reference;
import org.openpatch.scratch.*;


public class ColorSetRGB {
  public ColorSetRGB() {
    Stage myStage = new Stage(600, 240);

    Color myColor = new Color();
    myColor.setRGB(30, 120, 220);
    myStage.setColor(myColor);
    System.out.println("That colour has the hue " + myColor.getH() + ".");
  }

  public static void main(String[] args) {
    new ColorSetRGB();
  }
}
