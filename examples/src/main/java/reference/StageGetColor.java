package reference;
import org.openpatch.scratch.*;


public class StageGetColor {
  public StageGetColor() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.setColor(Color.fromHSB(55, 100, 100));
    Color background = myStage.getColor();
    System.out.println("hue " + background.getHue() + ", red " + background.getRed());
  }

  public static void main(String[] args) {
    new StageGetColor();
  }
}
