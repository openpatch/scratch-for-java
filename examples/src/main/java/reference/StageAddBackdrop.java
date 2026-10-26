package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class StageAddBackdrop {
  public StageAddBackdrop() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background", true);
    myStage.waitSeconds(1);
  }

  public static void main(String[] args) {
    new StageAddBackdrop();
  }
}
