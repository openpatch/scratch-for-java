package reference;
import org.openpatch.scratch.*;

public class StageIsMouseDown {
  public StageIsMouseDown() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    while (true) {
      myStage.display("Mouse down? " + myStage.isMouseDown());
    }
  }

  public static void main(String[] args) {
    new StageIsMouseDown();
  }
}
