package reference;
import org.openpatch.scratch.*;


public class ColorSetRGB {
  public ColorSetRGB() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Color myColor = new Color();
    myColor.setRGB(30, 120, 220);
    myStage.setColor(myColor);
    System.out.println("That colour has the hue " + myColor.getH() + ".");
  }

  public static void main(String[] args) {
    new ColorSetRGB();
  }
}
