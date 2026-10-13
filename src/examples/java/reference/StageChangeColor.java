package reference;
import org.openpatch.scratch.*;

public class StageChangeColor {
  public StageChangeColor() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.wait(2000);
    myStage.changeColor(200);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageChangeColor();
  }
}
