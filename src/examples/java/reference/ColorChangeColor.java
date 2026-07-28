package reference;
import org.openpatch.scratch.*;


public class ColorChangeColor {
  public ColorChangeColor() {
    Stage myStage = new Stage(600, 240);

    Color myColor = new Color(0);
    myStage.setColor(myColor);
    // Turning the hue all the way round, back to where it started.
    while (true) {
      myColor.changeColor(1);
      myStage.setColor(myColor);
      myStage.wait(30);
    }
  }

  public static void main(String[] args) {
    new ColorChangeColor();
  }
}
