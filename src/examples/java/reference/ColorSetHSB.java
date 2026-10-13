package reference;
import org.openpatch.scratch.*;


public class ColorSetHSB {
  public ColorSetHSB() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Color myColor = new Color();
    // Only the hue, which keeps the saturation and the brightness as they were.
    myColor.setHSB(140);
    myStage.setColor(myColor);
    myStage.wait(2000);
    // Hue, saturation and brightness at once: a pale, dark green.
    myColor.setHSB(140, 80, 120);
    myStage.setColor(myColor);
  }

  public static void main(String[] args) {
    new ColorSetHSB();
  }
}
