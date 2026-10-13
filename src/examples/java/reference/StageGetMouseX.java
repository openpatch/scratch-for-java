package reference;
import org.openpatch.scratch.*;


public class StageGetMouseX {
  public StageGetMouseX() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    while (true) {
      myStage.display("mouse x: " + myStage.getMouseX());
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new StageGetMouseX();
  }
}
