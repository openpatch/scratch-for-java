package reference;
import org.openpatch.scratch.*;

public class StageGetHeight {
  public StageGetHeight() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.display("Height: " + myStage.getHeight());
    myStage.waitSeconds(0.5);
  }

  public static void main(String[] args) {
    new StageGetHeight();
  }
}
