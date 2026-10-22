package reference;
import org.openpatch.scratch.*;

public class StageSetColor {
  public StageSetColor() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.waitSeconds(2);
    myStage.setColor(Color.fromHSB(78, 100, 100));
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new StageSetColor();
  }
}
