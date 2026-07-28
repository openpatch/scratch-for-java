package reference;
import org.openpatch.scratch.*;

public class StageSetColor {
  public StageSetColor() {
    Stage myStage = new Stage(600, 240);
    myStage.wait(2000);
    myStage.setColor(200);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageSetColor();
  }
}
