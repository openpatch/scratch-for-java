package reference;
import org.openpatch.scratch.*;

public class StageIsKeyPressed {
  public StageIsKeyPressed() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    while (true) {
      myStage.display("Space pressed? " + myStage.isKeyPressed(KeyCode.SPACE));
    }
  }

  public static void main(String[] args) {
    new StageIsKeyPressed();
  }
}
