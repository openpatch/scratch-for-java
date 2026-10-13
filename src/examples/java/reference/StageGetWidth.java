package reference;
import org.openpatch.scratch.*;

public class StageGetWidth {
  public StageGetWidth() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.display("Width: " + myStage.getWidth());
    myStage.wait(500);
    myStage.wait(500);
  }

  public static void main(String[] args) {
    new StageGetWidth();
  }
}
