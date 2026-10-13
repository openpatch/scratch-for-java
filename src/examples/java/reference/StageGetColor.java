package reference;
import org.openpatch.scratch.*;


public class StageGetColor {
  public StageGetColor() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.setColor(140);
    Color background = myStage.getColor();
    System.out.println("hue " + background.getH() + ", red " + background.getRed());
  }

  public static void main(String[] args) {
    new StageGetColor();
  }
}
