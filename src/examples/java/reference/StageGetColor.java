package reference;
import org.openpatch.scratch.*;


public class StageGetColor {
  public StageGetColor() {
    Stage myStage = new Stage(600, 240);

    myStage.setColor(140);
    Color background = myStage.getColor();
    System.out.println("hue " + background.getH() + ", red " + background.getRed());
  }

  public static void main(String[] args) {
    new StageGetColor();
  }
}
