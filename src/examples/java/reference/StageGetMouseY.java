package reference;
import org.openpatch.scratch.*;


public class StageGetMouseY {
  public StageGetMouseY() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    while (true) {
      myStage.display("mouse y: " + myStage.getMouseY());
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new StageGetMouseY();
  }
}
