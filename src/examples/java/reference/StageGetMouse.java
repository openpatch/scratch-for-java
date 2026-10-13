package reference;
import org.openpatch.scratch.*;

public class StageGetMouse {
  public StageGetMouse() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    while (true) {
      var mouseX = myStage.getMouseX();
      var mouseY = myStage.getMouseY();

      myStage.display("X: " + mouseX + " Y: " + mouseY);
    }
  }

  public static void main(String[] args) {
    new StageGetMouse();
  }
}
