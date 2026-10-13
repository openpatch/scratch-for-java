package reference;
import org.openpatch.scratch.*;

public class StageChangeTint {
  public StageChangeTint() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    myStage.wait(2000);
    myStage.changeTint(200);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageChangeTint();
  }
}
