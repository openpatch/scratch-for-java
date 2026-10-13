package reference;
import org.openpatch.scratch.*;

public class StageSetTint {
  public StageSetTint() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    myStage.wait(2000);
    myStage.setTint(200);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageSetTint();
  }
}
