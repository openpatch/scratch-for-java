package reference;
import org.openpatch.scratch.*;

public class StageChangeColor {
  public StageChangeColor() {
    Stage myStage = new Stage(600, 240);
    myStage.wait(2000);
    myStage.changeColor(200);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageChangeColor();
  }
}
